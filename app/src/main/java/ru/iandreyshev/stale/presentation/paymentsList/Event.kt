package ru.iandreyshev.stale.presentation.paymentsList

import ru.iandreyshev.stale.domain.core.PaymentId

sealed interface Event {
    data class NavigateToPayment(val id: PaymentId) : Event
    data class NavigateToPaymentEditor(val id: PaymentId? = null) : Event
    data class NavigateToTransactionEditor(val paymentId: PaymentId) : Event
    data class ShowPaymentDeletingDialog(val id: PaymentId, val title: String) : Event
}
