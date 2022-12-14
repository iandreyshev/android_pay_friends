package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.computationEditor.PaymentDraftError

sealed interface Result<out TResult> {
    data class Success<out T>(val data: T) : Result<T>
    data class Error(val error: ErrorType) : Result<Nothing>
}

sealed interface ErrorType {
    // Payment editor
    data class InvalidPaymentDraft(
        val errors: List<PaymentDraftError>
    ) : ErrorType

    // Bill editor
    object InvalidCost : ErrorType

    // Common
    object Unknown : ErrorType
}
