package ru.iandreyshev.payfriends.presentation.computationsList

import ru.iandreyshev.payfriends.domain.computationsList.ComputationSummary

data class State(
    val isListOfActivePayments: Boolean,
    val computations: List<ComputationSummary>,
    val isLoading: Boolean
) {

    companion object {
        fun default() = State(
            isListOfActivePayments = true,
            computations = listOf(),
            isLoading = false
        )
    }

}
