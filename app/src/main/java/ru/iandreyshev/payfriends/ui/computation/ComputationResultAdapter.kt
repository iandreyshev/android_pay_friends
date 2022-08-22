package ru.iandreyshev.payfriends.ui.computation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.ItemPaymentResultBinding
import ru.iandreyshev.payfriends.domain.core.Transfer

class ComputationResultAdapter(
    private val isResult: Boolean
) : ListAdapter<Transfer, ComputationResultAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(val binding: ItemPaymentResultBinding) : RecyclerView.ViewHolder(binding.root)

    object ItemCallback : DiffUtil.ItemCallback<Transfer>() {
        override fun areItemsTheSame(oldItem: Transfer, newItem: Transfer) = true
        override fun areContentsTheSame(oldItem: Transfer, newItem: Transfer) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_result, parent, false)
            .let { ItemPaymentResultBinding.bind(it) }
            .let { ViewHolder(it) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (isResult) {
            true -> bindResult(item, holder.binding)
            false -> bindTransfer(item, holder.binding)
        }
    }

    private fun bindResult(transfer: Transfer, binding: ItemPaymentResultBinding) {
        val res = binding.root.resources
        binding.firstMemberTitle.text = res.getString(R.string.computation_receiver_title)
        binding.firstMember.text = transfer.participants.receiver.name

        binding.secondMemberTitle.text = res.getString(R.string.computation_producer_title)
        binding.secondMember.text = transfer.participants.backer.name

        binding.cost.text = transfer.cost.toString()

        binding.description.isVisible = false
    }

    private fun bindTransfer(transfer: Transfer, binding: ItemPaymentResultBinding) {
        val res = binding.root.resources
        binding.firstMemberTitle.text = res.getString(R.string.computation_producer_title)
        binding.firstMember.text = transfer.participants.backer.name

        binding.secondMemberTitle.text = res.getString(R.string.computation_receiver_title)
        binding.secondMember.text = transfer.participants.receiver.name

        binding.cost.text = transfer.cost.toString()

//        binding.description.isVisible = transaction.description.isNotEmpty()
//        binding.description.text = transaction.description
    }

}