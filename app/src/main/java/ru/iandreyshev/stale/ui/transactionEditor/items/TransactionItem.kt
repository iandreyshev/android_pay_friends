package ru.iandreyshev.stale.ui.transactionEditor.items

import ru.iandreyshev.stale.domain.core.Member

data class TransactionItem(
    val receiver: Member,
    val cost: Int,
    val description: String
)
