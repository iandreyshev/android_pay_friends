package ru.iandreyshev.stale.ui.transactionEditor.items

import android.text.Editable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.ItemTransactionEditorProducerBinding
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.ui.members.MembersAdapter

data class ProducerFieldItem(
    val producer: Member?,
    val cost: Int,
    val suggestions: List<Member>,
    val candidate: String,
    val isTransactionsListEmpty: Boolean
) : TransactionEditorItem {

    val hasSuggestions: Boolean
        get() = suggestions.isNotEmpty()

}

fun producerAdapterDelegate(
    onProducerFieldTextChanged: (Editable?) -> Unit,
    onSuggestionClick: (Member) -> Unit,
    onAddButtonClick: (String) -> Unit,
    onRemoveProducerClick: () -> Unit
) = adapterDelegateViewBinding<ProducerFieldItem, TransactionEditorItem, ItemTransactionEditorProducerBinding>(
    viewBinding = { inflater, root ->
        inflater.inflate(R.layout.item_transaction_editor_producer, root, false)
            .let(ItemTransactionEditorProducerBinding::bind)
    }) {
    bind {
        val producer = item.producer
        binding.producer.isVisible = producer != null
        binding.producer.setState(name = producer?.name.orEmpty()) { onRemoveProducerClick() }

        binding.producerField.isVisible = producer == null
        binding.producerField.doAfterTextChanged(onProducerFieldTextChanged)

        binding.suggestions.isVisible = producer == null && item.hasSuggestions
        when (val adapter = binding.suggestions.adapter as? MembersAdapter) {
            null -> {
                val newAdapter = MembersAdapter(
                    onMemberClick = { member, _ -> onSuggestionClick(member) }
                )
                newAdapter.submitList(item.suggestions)
                binding.suggestions.adapter = newAdapter
                binding.suggestions.itemAnimator = null
                val itemDecoration = CandidatesItemDecoration(binding.root.resources)
                binding.suggestions.addItemDecoration(itemDecoration)
            }
            else ->
                adapter.submitList(item.suggestions) {
                    binding.suggestions.scrollToPosition(0)
                }
        }

        binding.addMemberButton.isVisible = producer == null && item.candidate.isNotEmpty()
        binding.addMemberButton.setState(name = item.candidate) {
            onAddButtonClick(binding.producerField.text.toString())
        }

        binding.transactionsEmptyView.isVisible = item.isTransactionsListEmpty

        binding.cost.isInvisible = producer == null
        binding.cost.text = getString(R.string.transaction_editor_cost, item.cost)
    }
}
