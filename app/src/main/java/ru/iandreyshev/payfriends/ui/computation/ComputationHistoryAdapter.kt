package ru.iandreyshev.payfriends.ui.computation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.ItemComputationHistoryBinding
import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.HistoryTransfer
import ru.iandreyshev.payfriends.domain.time.Date

class ComputationHistoryAdapter(
    private val onOpenOptionsMenu: (View, BillId) -> Unit
) : ListAdapter<ComputationHistoryAdapter.Item, ComputationHistoryAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(val binding: ItemComputationHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    data class Item(
        val id: BillId,
        val title: String,
        val number: Int,
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

        binding.cost.text = binding.root.resources.getString(R.string.common_cost_template, item.transfer.transfer.cost)

        binding.description.isVisible = item.transfer.description.isNotBlank()
        binding.description.text = item.transfer.description

        binding.billTitle.isVisible = item.isFirstInBill
        binding.billTitle.text = when {
            item.title.isNotBlank() -> item.title
            else -> res.getString(R.string.bill_editor_bill_default_title, item.number)
        }

        binding.bottomSeparator.isVisible = !item.isLastInBill

        binding.optionsButton.isVisible = item.isFirstInBill
        binding.optionsButton.setOnClickListener {
            onOpenOptionsMenu(it, item.id)
        }

        binding.root.updatePadding(
            bottom = when {
                item.isLastInBill -> binding.root.resources.getDimensionPixelSize(R.dimen.step_20)
                item.isLastInLastBill -> binding.root.resources.getDimensionPixelSize(R.dimen.step_64)
                else -> 0
            }
        )
    }

}