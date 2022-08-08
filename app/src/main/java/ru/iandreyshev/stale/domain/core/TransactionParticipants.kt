package ru.iandreyshev.stale.domain.core

data class TransactionParticipants(
    val producer: Member,
    val receiver: Member
) {

    fun mirror() = TransactionParticipants(producer = receiver, receiver = producer)

}
