package ru.iandreyshev.payfriends.ui.computation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.ItemComputationResultBinding
import ru.iandreyshev.payfriends.domain.core.HistoryTransfer
import ru.iandreyshev.payfriends.ui.utils.DateFormatter

class ComputationHistoryAdapter : ListAdapter<HistoryTransfer, ComputationHistoryAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(val binding: ItemComputationResultBinding) : RecyclerView.ViewHolder(binding.root)

    object ItemCallback : DiffUtil.ItemCallback<HistoryTransfer>() {
        override fun areItemsTheSame(oldItem: HistoryTransfer, newItem: HistoryTransfer) = true
        override fun areContentsTheSame(oldItem: HistoryTransfer, newItem: HistoryTransfer) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_computation_result, parent, false)
            .let { ItemComputationResultBinding.bind(it) }
            .let { ViewHolder(it) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transfer = getItem(position)
        val binding = holder.binding
        val res = binding.root.resources
        binding.firstMemberTitle.text = res.getString(R.string.computation_producer_title)
        binding.firstMember.text = transfer.transfer.participants.backer.name

        binding.secondMemberTitle.text = res.getString(R.string.computation_receiver_title)
        binding.secondMember.text = transfer.transfer.participants.receiver.name

        binding.cost.text = transfer.transfer.cost.toString()

        binding.description.isVisible = transfer.description.isNotBlank()
        binding.description.text = transfer.description

        binding.date.text = DateFormatter.format1(transfer.date)
    }

}