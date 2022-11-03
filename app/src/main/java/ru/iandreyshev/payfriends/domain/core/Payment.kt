package ru.iandreyshev.payfriends.domain.core

data class Payment(
    val id: PaymentId,
    val receiver: Member,
    val cost: Int,
    val description: String,
) {

    companion object {
        fun empty(
            id: PaymentId = PaymentId.none(),
            receiver: Member
        ) = Payment(
            id = id,
            receiver = receiver,
            cost = 0,
            description = ""
        )
    }

}
