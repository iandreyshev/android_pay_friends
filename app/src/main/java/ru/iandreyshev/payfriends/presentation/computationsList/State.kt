package ru.iandreyshev.payfriends.presentation.computationsList

import ru.iandreyshev.payfriends.domain.computationsList.ComputationSummary

data class State(
    val isCompleted: Boolean,
    val computations: List<ComputationSummary>,
    val isLoading: Boolean
) {

    companion object {
        fun default() = State(
            isCompleted = true,
            computations = listOf(),
            isLoading = false
        )
    }

}
