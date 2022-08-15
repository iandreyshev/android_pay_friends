package ru.iandreyshev.stale.ui.transactionEditor.items

import android.text.Editable
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.ItemTransactionEditorProducerBinding
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.ui.members.MembersAdapter

data class ProducerItem(
    val producer: Member?,
    val totalCost: Int,
    val suggestions: List<Member>,
    val candidate: String,
) : TransactionEditorItem

fun producerAdapterDelegate(
    onProducerFieldTextChanged: (Editable?) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onAddButtonClick: () -> Unit,
) = adapterDelegateViewBinding<ProducerItem, TransactionEditorItem, ItemTransactionEditorProducerBinding>(
    viewBinding = { inflater, root ->
        inflater.inflate(R.layout.item_transaction_editor_producer, root, false)
            .let(ItemTransactionEditorProducerBinding::bind)
    }) {
    bind {
        when (val producer = item.producer) {
            null -> {
                binding.producer.isVisible = false

                binding.producerField.isVisible = true
                binding.producerField.doAfterTextChanged(onProducerFieldTextChanged)

                binding.suggestionsList.isVisible = item.suggestions.isNotEmpty()
                when (val adapter = binding.suggestionsList.adapter as? MembersAdapter) {
                    null -> {
                        val newAdapter = MembersAdapter { member, _ -> onSuggestionClick(member.name) }
                        newAdapter.submitList(item.suggestions)
                        binding.suggestionsList.adapter = newAdapter
                        binding.suggestionsList.itemAnimator = null
                    }
                    else -> {
                        adapter.submitList(item.suggestions)
                    }
                }

                binding.addMemberButton.isVisible = item.candidate.isNotEmpty()
                binding.addMemberButton.setState(name = item.candidate) { onAddButtonClick() }
            }
            else -> {
                binding.producer.isVisible = true
                binding.producer.setState(name = producer.name)

                binding.producerField.isVisible = false

                binding.suggestionsList.isVisible = false

                binding.addMemberButton.isVisible = false
            }
        }
    }
}
