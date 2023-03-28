package ru.iandreyshev.payfriends.domain.core

data class Participants(
    val producer: Member,
    val receiver: Member,
) {

    fun mirror() = copy(producer = receiver, receiver = producer)

}
