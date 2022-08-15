package ru.iandreyshev.stale.ui.transactionEditor.items

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.ItemTransactionEditorTransactionBinding
import ru.iandreyshev.stale.domain.core.Member

data class TransactionItem(
    val member: Member,
    val cost: Int,
    val description: String,
    val showHeader: Boolean
) : TransactionEditorItem

fun transactionAdapterDelegate() =
    adapterDelegateViewBinding<TransactionItem, TransactionEditorItem, ItemTransactionEditorTransactionBinding>(
        viewBinding = { inflater, root ->
            inflater.inflate(R.layout.item_transaction_editor_transaction, root, false)
                .let(ItemTransactionEditorTransactionBinding::bind)
        }) {
        bind {
        }
    }
