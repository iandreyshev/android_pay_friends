package ru.iandreyshev.payfriends.domain.core

data class Transfer(
    val participants: Participants,
    val cost: Int
) {

    fun isMirrorOf(other: Transfer) =
        this == other.mirror()

    private fun mirror() = copy(participants = participants.mirror())

}
