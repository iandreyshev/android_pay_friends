package ru.iandreyshev.stale.domain.core

data class Transaction(
    val participants: TransactionParticipants,
    val cost: Int,
    val description: String = ""
) {

    fun isMirrorOf(transaction: Transaction) =
        participants == transaction.participants.mirror()

}
