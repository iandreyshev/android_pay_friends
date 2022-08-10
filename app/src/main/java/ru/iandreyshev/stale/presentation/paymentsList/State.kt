package ru.iandreyshev.stale.presentation.paymentsList

import ru.iandreyshev.stale.domain.payments.PaymentSummary

data class State(
    val payments: List<PaymentSummary>,
    val isLoading: Boolean
) {

    companion object {
        fun default() = State(
            payments = listOf(),
            isLoading = false
        )
    }

}
