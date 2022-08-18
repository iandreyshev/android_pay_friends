package ru.iandreyshev.stale.presentation.paymentsList

import ru.iandreyshev.stale.domain.payments.PaymentSummary

data class State(
    val isListOfActivePayments: Boolean,
    val payments: List<PaymentSummary>,
    val isLoading: Boolean
) {

    companion object {
        fun default() = State(
            isListOfActivePayments = true,
            payments = listOf(),
            isLoading = false
        )
    }

}
