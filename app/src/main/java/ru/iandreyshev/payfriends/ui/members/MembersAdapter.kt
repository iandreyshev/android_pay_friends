package ru.iandreyshev.payfriends.ui.members

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.ItemPaymentEditorMemberBinding
import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.ui.utils.uiLazy

class MembersAdapter(
    private val onMemberClick: (Member, Int) -> Unit = { _, _ -> },
    private val onRemoveMemberClick: ((Member, Int) -> Unit)? = null
) : ListAdapter<Member, MembersAdapter.ViewHolder>(ItemCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding by uiLazy { ItemPaymentEditorMemberBinding.bind(itemView) }
    }

    object ItemCallback : DiffUtil.ItemCallback<Member>() {
        override fun areItemsTheSame(oldItem: Member, newItem: Member) = true
        override fun areContentsTheSame(oldItem: Member, newItem: Member) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_editor_member, parent, false)
            .let { ViewHolder(it) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = getItem(position)
        holder.binding.memberView.setState(
            name = member.name,
            onClickListener = { onMemberClick(member, position) },
            onRemoveListener = onRemoveMemberClick?.let { block ->
                { block(member, position) }
            }
        )
    }

}
