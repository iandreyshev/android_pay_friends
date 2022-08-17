package ru.iandreyshev.stale.presentation.paymentEditor

import ru.iandreyshev.stale.domain.core.ErrorType
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.paymentEditor.PaymentDraftError

sealed interface Event {
    data class NavigateToPayment(val id: PaymentId, val name: String) : Event
    object ClearMemberField : Event
    data class ShowError(val error: ErrorType) : Event
    object Back : Event
    object BackWithError : Event
}
