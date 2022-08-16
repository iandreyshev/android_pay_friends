package ru.iandreyshev.stale.domain.core

data class Transaction(
    val id: TransactionId,
    val participants: TransactionParticipants,
    val cost: Int,
    val description: String = ""
) {

    fun isMirrorOf(transaction: Transaction) =
        participants == transaction.participants.mirror()

    companion object {
        fun empty(
            id: TransactionId = TransactionId(""),
            participants: TransactionParticipants
        ) = Transaction(
            id = id,
            participants = participants,
            cost = 0,
            description = ""
        )
    }

}
