package ru.iandreyshev.payfriends.ui.computation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.ItemComputationHistoryBinding
import ru.iandreyshev.payfriends.domain.core.HistoryTransfer
import ru.iandreyshev.payfriends.domain.time.Date
import ru.iandreyshev.payfriends.ui.utils.DateFormatter

class ComputationHistoryAdapter :
    ListAdapter<ComputationHistoryAdapter.Item, ComputationHistoryAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(val binding: ItemComputationHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    data class Item(
        val transfer: HistoryTransfer,
        val isFirstInBill: Boolean,
        val isLastInBill: Boolean,
        val isLastInLastBill: Boolean,
        val billDate: Date
    )

    object ItemCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) = true
        override fun areContentsTheSame(oldItem: Item, newItem: Item) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_computation_history, parent, false)
            .let { ItemComputationHistoryBinding.bind(it) }
            .let { ViewHolder(it) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val binding = holder.binding
        val res = binding.root.resources
        binding.firstMemberTitle.text = res.getString(R.string.computation_producer_title)
        binding.firstMember.text = item.transfer.transfer.participants.backer.name

        binding.secondMemberTitle.text = res.getString(R.string.computation_receiver_title)
        binding.secondMember.text = item.transfer.transfer.participants.receiver.name

        binding.cost.text = item.transfer.transfer.cost.toString()

        binding.description.isVisible = item.transfer.description.isNotBlank()
        binding.description.text = item.transfer.description

        binding.billTitle.isVisible = item.isFirstInBill
        binding.billTitle.text = res.getString(R.string.bill_editor_bill_title, DateFormatter.format1(item.billDate))

        binding.bottomSeparator.isVisible = !item.isLastInBill

        binding.root.updatePadding(bottom = when {
            item.isLastInLastBill -> LAST_ITEM_BOTTOM_PADDING
            else -> 0
        })
    }

    companion object {
        private const val LAST_ITEM_BOTTOM_PADDING = 300
    }

}