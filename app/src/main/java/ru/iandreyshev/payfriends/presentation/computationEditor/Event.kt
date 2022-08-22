package ru.iandreyshev.payfriends.presentation.computationEditor

import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.ComputationId

sealed interface Event {
    data class NavigateToPayment(val id: ComputationId, val name: String) : Event
    object ClearMemberField : Event
    data class ShowError(val error: ErrorType) : Event
    object Back : Event
    object BackWithError : Event
}
