package ru.iandreyshev.payfriends.ui.computation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.ItemComputationResultBinding
import ru.iandreyshev.payfriends.domain.core.Transfer

class ComputationResultAdapter : ListAdapter<Transfer, ComputationResultAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(val binding: ItemComputationResultBinding) : RecyclerView.ViewHolder(binding.root)

    object ItemCallback : DiffUtil.ItemCallback<Transfer>() {
        override fun areItemsTheSame(oldItem: Transfer, newItem: Transfer) = true
        override fun areContentsTheSame(oldItem: Transfer, newItem: Transfer) =
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

        binding.firstMemberTitle.text = res.getString(R.string.computation_receiver_title)
        binding.firstMember.text = transfer.participants.receiver.name

        binding.secondMemberTitle.text = res.getString(R.string.computation_result_producer_title)
        binding.secondMember.text = transfer.participants.backer.name

        binding.cost.text = binding.root.resources.getString(R.string.common_cost_template, transfer.cost)
    }

}