package ru.iandreyshev.payfriends.presentation.computationsList

import ru.iandreyshev.payfriends.domain.core.ComputationId

sealed interface Event {
    data class NavigateToPayment(val id: ComputationId, val name: String) : Event
    data class NavigateToComputationEditor(val id: ComputationId? = null) : Event
    data class NavigateToBillEditor(val computationId: ComputationId) : Event
    data class ShowPaymentDeletingDialog(val id: ComputationId, val title: String) : Event
}
