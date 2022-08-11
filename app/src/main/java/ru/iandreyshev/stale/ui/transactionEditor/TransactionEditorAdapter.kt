package ru.iandreyshev.stale.ui.transactionEditor

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.ItemTransactionEditorProducerBinding
import ru.iandreyshev.stale.databinding.ItemTransactionEditorTransactionBinding
import ru.iandreyshev.stale.domain.core.Member

interface State

data class ProducerState(
    val producer: Member?,
    val totalCost: Int
) : State

data class TransactionState(
    val member: Member,
    val cost: Int,
    val description: Int,
    val showHeader: Boolean,
    val showFooter: Boolean
) : State

val producerAdapterDelegate = adapterDelegateViewBinding<ProducerState, State, ItemTransactionEditorProducerBinding>(
    viewBinding = { inflater, root ->
        inflater.inflate(R.layout.item_transaction_editor_producer, root, false)
            .let(ItemTransactionEditorProducerBinding::bind)
    }) {
    bind {

    }
}

val transactionAdapterDelegate = adapterDelegateViewBinding<TransactionState, State, ItemTransactionEditorTransactionBinding>(
    viewBinding = { inflater, root ->
        inflater.inflate(R.layout.item_transaction_editor_transaction, root, false)
            .let(ItemTransactionEditorTransactionBinding::bind)
    }) {

}
