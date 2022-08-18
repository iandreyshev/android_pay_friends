package ru.iandreyshev.stale.presentation.payment

import ru.iandreyshev.stale.domain.core.ErrorType
import ru.iandreyshev.stale.domain.core.PaymentId

sealed interface Event {
    data class OpenTransactionEditor(val id: PaymentId) : Event
    data class Exit(val error: ErrorType? = null) : Event
}
