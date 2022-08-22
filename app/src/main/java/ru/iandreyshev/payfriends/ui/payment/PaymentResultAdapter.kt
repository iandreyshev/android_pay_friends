package ru.iandreyshev.payfriends.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.ItemPaymentResultBinding
import ru.iandreyshev.payfriends.domain.core.Transaction

class PaymentResultAdapter(
    private val isResult: Boolean
) : ListAdapter<Transaction, PaymentResultAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(val binding: ItemPaymentResultBinding) : RecyclerView.ViewHolder(binding.root)

    object ItemCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) = true
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) =
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
            false -> bindTransaction(item, holder.binding)
        }
    }

    private fun bindResult(transaction: Transaction, binding: ItemPaymentResultBinding) {
        val res = binding.root.resources
        binding.firstMemberTitle.text = res.getString(R.string.payment_receiver_title)
        binding.firstMember.text = transaction.participants.receiver.name

        binding.secondMemberTitle.text = res.getString(R.string.payment_producer_title)
        binding.secondMember.text = transaction.participants.producer.name

        binding.cost.text = transaction.cost.toString()

        binding.description.isVisible = false
    }

    private fun bindTransaction(transaction: Transaction, binding: ItemPaymentResultBinding) {
        val res = binding.root.resources
        binding.firstMemberTitle.text = res.getString(R.string.payment_producer_title)
        binding.firstMember.text = transaction.participants.producer.name

        binding.secondMemberTitle.text = res.getString(R.string.payment_receiver_title)
        binding.secondMember.text = transaction.participants.receiver.name

        binding.cost.text = transaction.cost.toString()

        binding.description.isVisible = transaction.description.isNotEmpty()
        binding.description.text = transaction.description
    }

}