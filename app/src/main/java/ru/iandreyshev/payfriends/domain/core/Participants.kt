package ru.iandreyshev.payfriends.domain.core

data class Participants(
    val backer: Member,
    val receiver: Member,
) {

    fun mirror() = copy(backer = receiver, receiver = backer)

}
