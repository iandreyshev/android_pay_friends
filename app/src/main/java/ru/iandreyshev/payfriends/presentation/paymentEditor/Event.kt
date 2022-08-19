package ru.iandreyshev.payfriends.presentation.paymentEditor

import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.PaymentId

sealed interface Event {
    data class NavigateToPayment(val id: PaymentId, val name: String) : Event
    object ClearMemberField : Event
    data class ShowError(val error: ErrorType) : Event
    object Back : Event
    object BackWithError : Event
}
