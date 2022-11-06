package ru.iandreyshev.payfriends.domain.core

data class Transfer(
    val participants: Participants,
    val cost: Int
) {

    fun isMirrorOf(other: Transfer) =
        participants == other.mirror().participants

    private fun mirror() = copy(participants = participants.mirror())

}
