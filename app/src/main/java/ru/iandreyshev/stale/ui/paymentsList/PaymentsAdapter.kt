package ru.iandreyshev.stale.ui.paymentsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.ItemPaymentBinding
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.payments.PaymentSummary

class PaymentsAdapter(
    private val onClick: (PaymentId) -> Unit,
    private val onAddTransaction: (PaymentId) -> Unit,
    private val onOptionsMenuOpen: (View, PaymentId) -> Unit
) : ListAdapter<PaymentSummary, PaymentsAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root)

    object ItemCallback : DiffUtil.ItemCallback<PaymentSummary>() {
        override fun areItemsTheSame(oldItem: PaymentSummary, newItem: PaymentSummary): Boolean = true
        override fun areContentsTheSame(oldItem: PaymentSummary, newItem: PaymentSummary): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment, parent, false)
            .let { ViewHolder(ItemPaymentBinding.bind(it)) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.title.text = item.name
        holder.binding.contentLayout.setOnClickListener {
            onClick(item.id)
        }
        holder.binding.addTransactionButton.setOnClickListener {
            onAddTransaction(item.id)
        }
        holder.binding.optionsButton.setOnClickListener {
            onOptionsMenuOpen(holder.binding.optionsButton, item.id)
        }
    }

}
