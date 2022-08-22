package ru.iandreyshev.payfriends.presentation.computation

import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.ComputationId

sealed interface Event {
    data class OpenBillEditor(val id: ComputationId) : Event
    data class Exit(val error: ErrorType? = null) : Event
}
