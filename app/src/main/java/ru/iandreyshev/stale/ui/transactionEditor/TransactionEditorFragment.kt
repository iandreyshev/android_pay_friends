package ru.iandreyshev.stale.ui.transactionEditor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.FragmentTransactionEditorBinding
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.TransactionId
import ru.iandreyshev.stale.presentation.transactionEditor.Intent
import ru.iandreyshev.stale.presentation.transactionEditor.Label
import ru.iandreyshev.stale.presentation.transactionEditor.State
import ru.iandreyshev.stale.presentation.transactionEditor.TransactionEditorViewModel
import ru.iandreyshev.stale.ui.transactionEditor.items.*
import ru.iandreyshev.stale.ui.utils.uiLazy
import ru.iandreyshev.stale.ui.utils.viewBindings
import ru.iandreyshev.stale.ui.utils.viewModelFactory

class TransactionEditorFragment : Fragment(R.layout.fragment_transaction_editor) {

    private val mViewModel by viewModelFactory {
        TransactionEditorViewModel(
            PaymentId(mArgs.paymentId),
            mArgs.transactionId?.let { TransactionId(it) }
        )
    }
    private val mArgs by navArgs<TransactionEditorFragmentArgs>()
    private val mBinding by viewBindings(FragmentTransactionEditorBinding::bind)
    private val mAdapter by uiLazy {
        ListDelegationAdapter(
            producerAdapterDelegate(
                onProducerFieldTextChanged = { mViewModel.onIntent(Intent.OnProducerFieldChanged(it.toString())) },
                onSuggestionClick = {},
                onAddButtonClick = {}
            ),
            transactionAdapterDelegate(),
            addTransactionAdapterDelegate()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mViewModel.state
                .onEach(::render)
                .launchIn(this)
            mViewModel.labels
                .onEach(::handleLabel)
                .launchIn(this)
        }
    }

    private fun initRecyclerView() {
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun render(state: State) {
        val items = mutableListOf<TransactionEditorItem>(
            ProducerItem(
                producer = state.producer,
                totalCost = state.totalCost,
                suggestions = state.producerSuggestions,
                query = state.producerSearchQuery,
                canAddNewMember = state.isAddingNewMemberActive
            )
        )

        state.transactions.forEachIndexed { index, transaction ->
            items.add(
                TransactionItem(
                    member = transaction.participants.receiver,
                    cost = transaction.cost,
                    description = transaction.description,
                    showHeader = index == 0
                )
            )
        }

        if (state.isNewTransactionsAvailable) {
            items.add(
                AddTransactionButtonItem(
                    query = state.newTransactionReceiverSearchQuery,
                    suggestions = state.newTransactionReceiverSuggestions
                )
            )
        }

        mAdapter.items = items
    }

    private fun handleLabel(label: Label) {
    }

}
