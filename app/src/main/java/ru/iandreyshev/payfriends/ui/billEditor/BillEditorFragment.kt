package ru.iandreyshev.payfriends.ui.billEditor

import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentBillEditorBinding
import ru.iandreyshev.payfriends.databinding.ItemBillEditorPaymentBinding
import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.presentation.billEditor.*
import ru.iandreyshev.payfriends.ui.billEditor.items.*
import ru.iandreyshev.payfriends.ui.members.MembersAdapter
import ru.iandreyshev.payfriends.ui.utils.*
import kotlin.math.abs

class BillEditorFragment : Fragment(R.layout.fragment_bill_editor) {

    private val viewModel by viewModelsDiFactory<BillEditorViewModel>()
    private val navController by uiLazy { findNavController() }
    private val binding by viewBindings(FragmentBillEditorBinding::bind)
    private val paymentMarginHorizontal by uiLazy { resources.getDimensionPixelSize(R.dimen.step_16) }
    private val paymentMarginVertical by uiLazy { resources.getDimensionPixelSize(R.dimen.step_8) }
    private val getDefaultTitle by uiLazy { DefaultBillTitleProvider(resources) }

    private var alertDialog: AlertDialog? = null
    private val costWatchers = mutableSetOf<TextWatcher>()
    private val descriptionWatchers = mutableSetOf<TextWatcher>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAppBar()
        initGestures()
        initTextFields()
        initTitle()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state
                .onEach(::render)
                .launchIn(this)
            viewModel.labels
                .onEach(::handleLabel)
                .launchIn(this)
        }

        if (savedInstanceState == null) {
            val args = navArgs<BillEditorFragmentArgs>().value
            val computationId = ComputationId(args.paymentId)
            val billId = args.billId?.let { BillId(it) }
            viewModel.onViewCreated(computationId, billId)
            binding.title.titleField.showKeyboard()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alertDialog.dismissOnDestroy()
    }

    private fun initGestures() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            viewModel(Intent.OnBack)
        }
    }

    private fun initAppBar() {
        binding.toolbar.setNavigationOnClickListener { viewModel(Intent.OnBack) }
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.billEditorMenuSave -> viewModel(Intent.OnSave)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun initTextFields() {
        binding.producerView.producerField.doAfterTextChanged {
            viewModel(Intent.OnProducerFieldChanged(it.toString()))
        }
        binding.receiverView.receiverField.doAfterTextChanged {
            viewModel(Intent.OnReceiverFieldChanged(it.toString()))
        }
    }

    private fun initTitle() {
        binding.title.titleField.doAfterTextChanged {
            viewModel(Intent.OnTitleChanged(it.toString()))
        }
    }

    private fun render(state: State) {
        renderBackerField(
            item = BackerFieldItem(
                backer = state.backerField.backer,
                cost = state.backerField.cost,
                suggestions = state.backerField.suggestions,
                candidateQuery = state.backerField.candidateQuery,
                candidate = state.backerField.candidate,
                isPaymentsEmptyViewVisible = state.payments.isEmpty()
            )
        )

        renderPayments(payments = state.payments.map { payment ->
            PaymentItem(
                receiver = payment.receiver,
                cost = payment.cost,
                description = payment.description,
            )
        })

        binding.toolbar.title = when {
            state.title.isBlank() -> getDefaultTitle(state.number)
            else -> state.title
        }

        if (state.receiverField.isEnabled) {
            renderReceiverField(
                ReceiverFieldItem(
                    suggestions = state.receiverField.suggestions,
                    candidateQuery = state.receiverField.candidateQuery,
                    candidate = state.receiverField.candidate,
                )
            )
        }
    }

    private fun renderBackerField(item: BackerFieldItem) {
        val binding = binding.producerView

        binding.backer.isVisible = item.backer != null
        binding.backer.setState(name = item.backer?.name.orEmpty()) {
            viewModel(Intent.OnRemoveProducer)
        }

        binding.producerField.isVisible = item.backer == null

        binding.suggestions.isVisible = item.backer == null && item.hasSuggestions
        val adapter = binding.suggestions.adapter as? MembersAdapter ?: run {
            val newAdapter = MembersAdapter(
                onMemberClick = { member, _ ->
                    viewModel(Intent.OnProducerSelected(member))
                }
            )
            newAdapter.submitList(item.suggestions)
            binding.suggestions.adapter = newAdapter
            binding.suggestions.itemAnimator = null
            val itemDecoration = BackerCandidatesItemDecoration(binding.root.resources)
            binding.suggestions.addItemDecoration(itemDecoration)
            newAdapter
        }
        adapter.submitList(item.suggestions) {
            binding.suggestions.scrollToPosition(0)
        }

        binding.addMemberButton.isVisible = item.backer == null && item.candidate.isNotEmpty()
        binding.addMemberButton.setState(name = item.candidate) {
            binding.producerField.text.toString()
            viewModel(Intent.OnProducerCandidateSelected(binding.producerField.text.toString()))
        }

        binding.paymentsEmptyView.isVisible = item.isPaymentsEmptyViewVisible

        binding.cost.isInvisible = item.backer == null
        binding.cost.text = getString(R.string.bill_editor_cost, item.cost)
    }

    private fun renderReceiverField(item: ReceiverFieldItem) {
        val binding = binding.receiverView

        binding.suggestions.isVisible = item.hasSuggestions
        val adapter = binding.suggestions.adapter as? MembersAdapter ?: run {
            val newAdapter = MembersAdapter(
                onMemberClick = { member, _ ->
                    viewModel(Intent.OnReceiverSelected(member))
                }
            )
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            binding.suggestions.layoutManager = layoutManager
            binding.suggestions.adapter = newAdapter
            binding.suggestions.itemAnimator = null
            val itemDecoration = ReceiverCandidatesItemDecoration(binding.root.resources)
            binding.suggestions.addItemDecoration(itemDecoration)
            newAdapter
        }
        adapter.submitList(item.suggestions) {
            binding.suggestions.scrollToPosition(0)
        }

        binding.addMemberButton.isVisible = item.candidate.isNotEmpty()
        binding.addMemberButton.setState(name = item.candidate) {
            binding.receiverField.text.toString()
            viewModel(Intent.OnReceiverCandidateSelected(binding.receiverField.text.toString()))
        }
    }

    private fun renderPayments(payments: List<PaymentItem>) {
        val viewCount = binding.payments.childCount
        val countDiff = abs(payments.count() - viewCount)

        when {
            viewCount < payments.count() -> repeat(countDiff) {
                ItemBillEditorPaymentBinding
                    .inflate(layoutInflater, binding.payments, true)
            }
            viewCount > payments.count() -> repeat(countDiff) {
                binding.payments.removeViewAt(viewCount - 1 - it)
            }
        }

        binding.payments.forEach { view ->
            val binding = ItemBillEditorPaymentBinding.bind(view)
            costWatchers.forEach { watcher ->
                binding.costField.removeTextChangedListener(watcher)
            }
            descriptionWatchers.forEach { watcher ->
                binding.descriptionField.removeTextChangedListener(watcher)
            }
        }

        payments.forEachIndexed { index, paymentItem ->
            val view = binding.payments[index]
            val binding = ItemBillEditorPaymentBinding.bind(view)
            binding.root.updateLayoutParams<LinearLayout.LayoutParams> {
                marginStart = paymentMarginHorizontal
                marginEnd = paymentMarginHorizontal
                bottomMargin = paymentMarginVertical
                if (index == 0) {
                    topMargin = paymentMarginVertical
                }
            }
            if (paymentItem.cost > 0) {
                binding.costField.setTextIfChanged(paymentItem.cost.toString())
            }
            binding.receiver.text = paymentItem.receiver.name
            costWatchers += binding.costField.doAfterTextChanged {
                viewModel(Intent.OnCostChanged(index, it.toString()))
            }
            descriptionWatchers += binding.descriptionField.doAfterTextChanged {
                viewModel(Intent.OnDescriptionChanged(index, it.toString()))
            }
            binding.removeButton.setOnClickListener {
                viewModel(Intent.OnRemovePayment(index))
            }
        }
    }

    private fun handleLabel(label: Label) {
        when (label) {
            is Label.Exit -> navController.popBackStack()
            Label.ExitWithWarning -> showExitDialog()
            Label.ScrollToBottom ->
                binding.root.post {
                    binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
            Label.Error.InvalidProducerCandidate ->
                toast(R.string.bill_editor_error_empty_name)
            Label.Error.InvalidCost ->
                toast(R.string.bill_editor_error_invalid_cost)
            Label.Error.Unknown ->
                toast(R.string.common_unknown_error)
        }
    }

    private fun showExitDialog() {
        alertDialog.dismissOnDestroy()
        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("После выхода все изменения будут потеряны")
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Выйти") { dialog, _ ->
                dialog.cancel()
                navController.popBackStack()
            }
            .show()
    }

}
