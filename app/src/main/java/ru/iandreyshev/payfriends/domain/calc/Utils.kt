package ru.iandreyshev.payfriends.domain.calc

import ru.iandreyshev.payfriends.domain.core.Participants
import ru.iandreyshev.payfriends.domain.core.Transfer

fun getResultParticipants(
    transfer1: Transfer,
    transfer2: Transfer
): Participants {
    if (transfer1.cost > transfer2.cost) {
        return transfer1.participants
    }

    return transfer2.participants
}
