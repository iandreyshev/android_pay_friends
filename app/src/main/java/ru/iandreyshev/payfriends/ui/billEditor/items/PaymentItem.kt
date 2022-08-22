package ru.iandreyshev.payfriends.ui.billEditor.items

import ru.iandreyshev.payfriends.domain.core.Member

data class PaymentItem(
    val receiver: Member,
    val cost: Int,
    val description: String
)
