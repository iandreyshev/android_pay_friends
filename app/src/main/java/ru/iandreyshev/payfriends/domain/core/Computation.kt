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

    fun getHistory() = bills.flatMap { bill ->
        bill.payments.map { payment ->
            HistoryTransfer(
                transfer = Transfer(
                    participants = Participants(
                        bill.backer,
                        payment.receiver
                    ),
                    cost = payment.cost
                ),
                description = payment.description,
                date = payment.creationDate
            )
        }
    }

}
