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
import ru.iandreyshev.payfriends.presentation.billEditor.BillEditorViewModel
import ru.iandreyshev.payfriends.presentation.billEditor.Intent
import ru.iandreyshev.payfriends.presentation.billEditor.Label
import ru.iandreyshev.payfriends.presentation.billEditor.State
import ru.iandreyshev.payfriends.ui.billEditor.items.*
import ru.iandreyshev.payfriends.ui.members.MembersAdapter
import ru.iandreyshev.payfriends.ui.utils.*
import kotlin.math.abs

class BillEditorFragment : Fragment(R.layout.fragment_bill_editor) {

    private val mViewModel by viewModelsDiFactory<BillEditorViewModel>()
    private val mNavController by uiLazy { findNavController() }
    private val mBinding by viewBindings(FragmentBillEditorBinding::bind)
    private val mPaymentMarginHorizontal by uiLazy { resources.getDimensionPixelSize(R.dimen.step_16) }
    private val mPaymentMarginVertical by uiLazy { resources.getDimensionPixelSize(R.dimen.step_8) }

    private var mAlertDialog: AlertDialog? = null
    private val mCostWatchers = mutableSetOf<TextWatcher>()
    private val mDescriptionWatchers = mutableSetOf<TextWatcher>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAppBar()
        initGestures()
        initTextFields()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mViewModel.state
                .onEach(::render)
                .launchIn(this)
            mViewModel.labels
                .onEach(::handleLabel)
                .launchIn(this)
        }

        if (savedInstanceState == null) {
            val args = navArgs<BillEditorFragmentArgs>().value
            val computationId = ComputationId(args.paymentId)
            val billId = args.billId?.let { BillId(it) }
            mViewModel.onViewCreated(computationId, billId)
            mBinding.producerView.producerField.showKeyboard()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAlertDialog.dismissOnDestroy()
    }

    private fun initGestures() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            mViewModel(Intent.OnBack)
        }
    }

    private fun initAppBar() {
        mBinding.toolbar.setNavigationOnClickListener { mViewModel(Intent.OnBack) }
        mBinding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.billEditorMenuSave -> mViewModel(Intent.OnSave)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun initTextFields() {
        mBinding.producerView.producerField.doAfterTextChanged {
            mViewModel(Intent.OnProducerFieldChanged(it.toString()))
        }
        mBinding.receiverView.receiverField.doAfterTextChanged {
            mViewModel(Intent.OnReceiverFieldChanged(it.toString()))
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
        val binding = mBinding.producerView

        binding.backer.isVisible = item.backer != null
        binding.backer.setState(name = item.backer?.name.orEmpty()) {
            mViewModel(Intent.OnRemoveProducer)
        }

        binding.producerField.isVisible = item.backer == null

        binding.suggestions.isVisible = item.backer == null && item.hasSuggestions
        val adapter = binding.suggestions.adapter as? MembersAdapter ?: run {
            val newAdapter = MembersAdapter(
                onMemberClick = { member, _ ->
                    mViewModel(Intent.OnProducerSelected(member))
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
            mViewModel(Intent.OnProducerCandidateSelected(binding.producerField.text.toString()))
        }

        binding.paymentsEmptyView.isVisible = item.isPaymentsEmptyViewVisible

        binding.cost.isInvisible = item.backer == null
        binding.cost.text = getString(R.string.bill_editor_cost, item.cost)
    }

    private fun renderReceiverField(item: ReceiverFieldItem) {
        val binding = mBinding.receiverView

        binding.suggestions.isVisible = item.hasSuggestions
        val adapter = binding.suggestions.adapter as? MembersAdapter ?: run {
            val newAdapter = MembersAdapter(
                onMemberClick = { member, _ ->
                    mViewModel(Intent.OnReceiverSelected(member))
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
            mViewModel(Intent.OnReceiverCandidateSelected(binding.receiverField.text.toString()))
        }
    }

    private fun renderPayments(payments: List<PaymentItem>) {
        val viewCount = mBinding.payments.childCount
        val countDiff = abs(payments.count() - viewCount)

        when {
            viewCount < payments.count() -> repeat(countDiff) {
                ItemBillEditorPaymentBinding
                    .inflate(layoutInflater, mBinding.payments, true)
            }
            viewCount > payments.count() -> repeat(countDiff) {
                mBinding.payments.removeViewAt(viewCount - 1 - it)
            }
        }

        mBinding.payments.forEach { view ->
            val binding = ItemBillEditorPaymentBinding.bind(view)
            mCostWatchers.forEach { watcher ->
                binding.costField.removeTextChangedListener(watcher)
            }
            mDescriptionWatchers.forEach { watcher ->
                binding.descriptionField.removeTextChangedListener(watcher)
            }
        }

        payments.forEachIndexed { index, paymentItem ->
            val view = mBinding.payments[index]
            val binding = ItemBillEditorPaymentBinding.bind(view)
            binding.root.updateLayoutParams<LinearLayout.LayoutParams> {
                marginStart = mPaymentMarginHorizontal
                marginEnd = mPaymentMarginHorizontal
                bottomMargin = mPaymentMarginVertical
                if (index == 0) {
                    topMargin = mPaymentMarginVertical
                }
            }
            if (paymentItem.cost > 0) {
                binding.costField.setTextIfChanged(paymentItem.cost.toString())
            }
            binding.receiver.text = paymentItem.receiver.name
            mCostWatchers += binding.costField.doAfterTextChanged {
                mViewModel(Intent.OnCostChanged(index, it.toString()))
            }
            mDescriptionWatchers += binding.descriptionField.doAfterTextChanged {
                mViewModel(Intent.OnDescriptionChanged(index, it.toString()))
            }
            binding.removeButton.setOnClickListener {
                mViewModel(Intent.OnRemovePayment(index))
            }
        }
    }

    private fun handleLabel(label: Label) {
        when (label) {
            is Label.Exit -> mNavController.popBackStack()
            Label.ExitWithWarning -> showExitDialog()
            Label.ScrollToBottom ->
                mBinding.root.post {
                    mBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
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
        mAlertDialog.dismissOnDestroy()
        mAlertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("После выхода все изменения будут потеряны")
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Выйти") { dialog, _ ->
                dialog.cancel()
                mNavController.popBackStack()
            }
            .show()
    }

}
