package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.time.Date

data class Computation(
    val id: ComputationId,
    val title: String,
    val bills: List<Bill>,
    val members: List<Member>,
    val creationDate: Date,
    val isCompleted: Boolean,
) {

    fun getAllTransfers() = bills.flatMap { bill ->
        bill.payments.map { payment ->
            Transfer(
                participants = Participants(
                    bill.backer,
                    payment.receiver
                ),
                cost = payment.cost
            )
        }
    }

}
