package ru.iandreyshev.stale.ui.paymentEditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.ItemPaymentEditorMemberBinding
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.ui.utils.uiLazy
import java.util.*

class MembersAdapter(
    private val onMemberClick: (Member, Int) -> Unit
) : ListAdapter<Member, MembersAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding by uiLazy { ItemPaymentEditorMemberBinding.bind(itemView) }
    }

    object ItemCallback : DiffUtil.ItemCallback<Member>() {
        override fun areItemsTheSame(oldItem: Member, newItem: Member) = true
        override fun areContentsTheSame(oldItem: Member, newItem: Member) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_editor_member, parent, false)
            .let { ViewHolder(it) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = getItem(position)
        holder.binding.apply {
            name.text = member.name
            firstChar.text = (member.name.firstOrNull() ?: "").toString()
                .replaceFirstChar { it.titlecase(Locale.getDefault()) }
            clickableArea.setOnClickListener { onMemberClick(member, position) }
        }
    }

}