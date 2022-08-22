package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.time.Date

data class Payment(
    val id: PaymentId,
    val receiver: Member,
    val cost: Int,
    val description: String,
    val creationDate: Date
) {

    companion object {
        fun empty(
            id: PaymentId = PaymentId.none(),
            receiver: Member
        ) = Payment(
            id = id,
            receiver = receiver,
            cost = 0,
            description = "",
            creationDate = Date("")
        )
    }

}
