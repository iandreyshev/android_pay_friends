package ru.iandreyshev.stale.presentation.payment

import ru.iandreyshev.stale.domain.core.ErrorType

sealed interface Event {
    data class Exit(val error: ErrorType?) : Event
}
