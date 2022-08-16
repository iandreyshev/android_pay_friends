package ru.iandreyshev.stale.ui.transactionEditor.items

import ru.iandreyshev.stale.domain.core.Member

data class TransactionItem(
    val member: Member,
    val cost: Int,
    val description: String,
    val showHeader: Boolean
)
