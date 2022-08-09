package ru.iandreyshev.stale.domain.payment

data class Payment(
    val id: PaymentId,
    val name: String,
    val creationDate: String,
    val isArchived: Boolean
)
