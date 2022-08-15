package ru.iandreyshev.stale.ui.transactionEditor.items

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.ItemAddTransactionButtonBinding
import ru.iandreyshev.stale.domain.core.Member

data class AddTransactionButtonItem(
    val query: String,
    val suggestions: List<Member>
) : TransactionEditorItem

fun addTransactionAdapterDelegate() =
    adapterDelegateViewBinding<AddTransactionButtonItem, TransactionEditorItem, ItemAddTransactionButtonBinding>(
        viewBinding = { inflater, root ->
            inflater.inflate(R.layout.item_add_transaction_button, root, false)
                .let(ItemAddTransactionButtonBinding::bind)
        }) {
        bind {
        }
    }

