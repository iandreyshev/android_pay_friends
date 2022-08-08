package ru.iandreyshev.stale.presentation.paymentsList

import ru.iandreyshev.stale.domain.payment.PaymentId

sealed interface Event

data class NavigateToPaymentEditor(val paymentId: PaymentId? = null) : Event
data class NavigateToTransactionEditor(val paymentId: PaymentId) : Event
data class ShowPaymentDeletingDialog(val paymentId: PaymentId, val title: String) : Event
