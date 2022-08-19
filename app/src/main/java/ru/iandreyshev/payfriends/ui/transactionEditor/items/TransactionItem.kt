package ru.iandreyshev.payfriends.ui.transactionEditor.items

import ru.iandreyshev.payfriends.domain.core.Member

data class TransactionItem(
    val receiver: Member,
    val cost: Int,
    val description: String
)
