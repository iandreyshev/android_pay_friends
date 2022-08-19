package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.time.Date

data class Transaction(
    val id: TransactionId,
    val participants: TransactionParticipants,
    val cost: Int,
    val description: String,
    val creationDate: Date
) {

    fun isMirrorOf(transaction: Transaction) =
        participants == transaction.participants.mirror()

    companion object {
        fun empty(
            id: TransactionId = TransactionId.none(),
            participants: TransactionParticipants
        ) = Transaction(
            id = id,
            participants = participants,
            cost = 0,
            description = "",
            creationDate = Date("")
        )
    }

}
