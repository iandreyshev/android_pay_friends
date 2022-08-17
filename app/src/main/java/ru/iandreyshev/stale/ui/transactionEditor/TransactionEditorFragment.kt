package ru.iandreyshev.stale.ui.transactionEditor

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
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
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.FragmentTransactionEditorBinding
import ru.iandreyshev.stale.databinding.ItemTransactionEditorTransactionBinding
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.TransactionId
import ru.iandreyshev.stale.presentation.transactionEditor.Intent
import ru.iandreyshev.stale.presentation.transactionEditor.Label
import ru.iandreyshev.stale.presentation.transactionEditor.State
import ru.iandreyshev.stale.presentation.transactionEditor.TransactionEditorViewModel
import ru.iandreyshev.stale.ui.members.MembersAdapter
import ru.iandreyshev.stale.ui.transactionEditor.items.*
import ru.iandreyshev.stale.ui.utils.*
import kotlin.math.abs

class TransactionEditorFragment : Fragment(R.layout.fragment_transaction_editor) {

    private val mViewModel by viewModelFactory {
        TransactionEditorViewModel(
            PaymentId(mArgs.paymentId),
            mArgs.transactionId?.let { TransactionId(it) }
        )
    }
    private val mNavController by uiLazy { findNavController() }
    private val mArgs by navArgs<TransactionEditorFragmentArgs>()
    private val mBinding by viewBindings(FragmentTransactionEditorBinding::bind)
    private val mTransactionMarginHorizontal by uiLazy { resources.getDimensionPixelSize(R.dimen.step_16) }
    private val mTransactionMarginVertical by uiLazy { resources.getDimensionPixelSize(R.dimen.step_8) }

    private var mAlertDialog: AlertDialog? = null

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAlertDialog.dismissOnDestroy()
    }

    private fun initGestures() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            showExitDialog()
        }
    }

    private fun initAppBar() {
        mBinding.toolbar.setNavigationOnClickListener { showExitDialog() }
        mBinding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.transactionEditorMenuSave -> mViewModel(Intent.OnSave)
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
        renderProducerField(
            item = ProducerFieldItem(
                producer = state.producerField.producer,
                cost = state.producerField.cost,
                suggestions = state.producerField.suggestions,
                candidateQuery = state.producerField.candidateQuery,
                candidate = state.producerField.candidate,
                isTransactionsEmptyViewVisible = state.transactions.isEmpty()
            )
        )

        renderTransactions(transactions = state.transactions.map { transaction ->
            TransactionItem(
                receiver = transaction.participants.receiver,
                cost = transaction.cost,
                description = transaction.description,
            )
        })

        if (state.receiverField.isEnabled) {
            renderReceiverField(
                ReceiverFieldItem(
                    receiver = state.receiverField.receiver,
                    suggestions = state.receiverField.suggestions,
                    candidateQuery = state.receiverField.candidateQuery,
                    candidate = state.receiverField.candidate,
                )
            )
        }
    }

    private fun renderProducerField(item: ProducerFieldItem) {
        val binding = mBinding.producerView

        binding.producer.isVisible = item.producer != null
        binding.producer.setState(name = item.producer?.name.orEmpty()) {
            mViewModel(Intent.OnRemoveProducer)
        }

        binding.producerField.isVisible = item.producer == null

        binding.suggestions.isVisible = item.producer == null && item.hasSuggestions
        val adapter = binding.suggestions.adapter as? MembersAdapter ?: run {
            val newAdapter = MembersAdapter(
                onMemberClick = { member, _ ->
                    mViewModel(Intent.OnProducerSelected(member))
                }
            )
            newAdapter.submitList(item.suggestions)
            binding.suggestions.adapter = newAdapter
            binding.suggestions.itemAnimator = null
            val itemDecoration = ProducerCandidatesItemDecoration(binding.root.resources)
            binding.suggestions.addItemDecoration(itemDecoration)
            newAdapter
        }
        adapter.submitList(item.suggestions) {
            binding.suggestions.scrollToPosition(0)
        }

        binding.addMemberButton.isVisible = item.producer == null && item.candidate.isNotEmpty()
        binding.addMemberButton.setState(name = item.candidate) {
            binding.producerField.text.toString()
            mViewModel(Intent.OnProducerCandidateSelected(binding.producerField.text.toString()))
        }

        binding.transactionsEmptyView.isVisible = item.isTransactionsEmptyViewVisible

        binding.cost.isInvisible = item.producer == null
        binding.cost.text = getString(R.string.transaction_editor_cost, item.cost)
    }

    private fun renderReceiverField(item: ReceiverFieldItem) {
        val binding = mBinding.receiverView

        binding.receiverField.isVisible = item.receiver == null

        binding.suggestions.isVisible = item.receiver == null && item.hasSuggestions
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

        binding.addMemberButton.isVisible = item.receiver == null && item.candidate.isNotEmpty()
        binding.addMemberButton.setState(name = item.candidate) {
            binding.receiverField.text.toString()
            mViewModel(Intent.OnReceiverCandidateSelected(binding.receiverField.text.toString()))
        }
    }

    private fun renderTransactions(transactions: List<TransactionItem>) {
        val viewCount = mBinding.transactions.childCount
        val countDiff = abs(transactions.count() - viewCount)

        when {
            viewCount < transactions.count() -> repeat(countDiff) {
                ItemTransactionEditorTransactionBinding
                    .inflate(layoutInflater, mBinding.transactions, true)
            }
            viewCount > transactions.count() -> repeat(countDiff) {
                mBinding.transactions.removeViewAt(viewCount - 1 - it)
            }
        }

        transactions.forEachIndexed { index, transactionItem ->
            val view = mBinding.transactions[index]
            val binding = ItemTransactionEditorTransactionBinding.bind(view)
            binding.root.updateLayoutParams<LinearLayout.LayoutParams> {
                marginStart = mTransactionMarginHorizontal
                marginEnd = mTransactionMarginHorizontal
                bottomMargin = mTransactionMarginVertical
                if (index == 0) {
                    topMargin = mTransactionMarginVertical
                }
            }
            binding.receiver.text = transactionItem.receiver.name
        }
    }

    private fun handleLabel(label: Label) {
        when (label) {
            is Label.Exit -> mNavController.popBackStack()
            Label.Error.InvalidProducerCandidate ->
                toast(R.string.transaction_editor_error_empty_name)
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
                mViewModel(Intent.OnExit)
            }
            .show()
    }

}
