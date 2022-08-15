package ru.iandreyshev.stale.ui.transactionEditor.items

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.ItemReceiverFieldButtonBinding
import ru.iandreyshev.stale.domain.core.Member

data class ReceiverFieldItem(
    val query: String,
    val suggestions: List<Member>
) : TransactionEditorItem

fun producerFieldAdapterDelegate() =
    adapterDelegateViewBinding<ReceiverFieldItem, TransactionEditorItem, ItemReceiverFieldButtonBinding>(
        viewBinding = { inflater, root ->
            inflater.inflate(R.layout.item_receiver_field_button, root, false)
                .let(ItemReceiverFieldButtonBinding::bind)
        }) {
        bind {
        }
    }

