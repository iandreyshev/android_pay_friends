package ru.iandreyshev.payfriends.ui.computationsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.ItemComputationBinding
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.computationsList.ComputationSummary
import ru.iandreyshev.payfriends.ui.utils.DateFormatter

class ComputationsAdapter(
    private val isCompleted: Boolean,
    private val onClick: (ComputationSummary) -> Unit,
    private val onAddBill: (ComputationId) -> Unit,
    private val onOptionsMenuOpen: (View, ComputationId) -> Unit
) : ListAdapter<ComputationSummary, ComputationsAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(val binding: ItemComputationBinding) : RecyclerView.ViewHolder(binding.root)

    object ItemCallback : DiffUtil.ItemCallback<ComputationSummary>() {
        override fun areItemsTheSame(oldItem: ComputationSummary, newItem: ComputationSummary): Boolean = true
        override fun areContentsTheSame(oldItem: ComputationSummary, newItem: ComputationSummary): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_computation, parent, false)
            .let { ViewHolder(ItemComputationBinding.bind(it)) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.title.text = item.title
        holder.binding.contentLayout.setOnClickListener {
            onClick(item)
        }
        holder.binding.date.text = DateFormatter.format2(item.date)
        holder.binding.addBillButton.isVisible = isCompleted
        holder.binding.addBillButton.setOnClickListener {
            onAddBill(item.id)
        }
        holder.binding.optionsButton.setOnClickListener {
            onOptionsMenuOpen(holder.binding.optionsButton, item.id)
        }
    }

}
