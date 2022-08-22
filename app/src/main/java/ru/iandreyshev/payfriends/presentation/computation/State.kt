package ru.iandreyshev.payfriends.presentation.computation

import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.core.Transfer
import ru.iandreyshev.payfriends.domain.time.Date

data class State(
    val id: ComputationId,
    val name: String,
    val creationDate: Date,
    val result: List<Transfer>,
    val history: List<Transfer>,
    val isLoading: Boolean
) {

    companion object {
        fun default() = State(
            id = ComputationId(""),
            name = "",
            creationDate = Date(""),
            result = emptyList(),
            history = emptyList(),
            isLoading = true
        )
    }

}
