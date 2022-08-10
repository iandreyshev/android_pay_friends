package ru.iandreyshev.stale.domain.core

data class Payment(
    val id: PaymentId,
    val name: String,
    val members: List<Member>,
    val creationDate: String,
    val isArchived: Boolean
)
