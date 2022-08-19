package ru.iandreyshev.payfriends.presentation.payment

import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.PaymentId

sealed interface Event {
    data class OpenTransactionEditor(val id: PaymentId) : Event
    data class Exit(val error: ErrorType? = null) : Event
}
