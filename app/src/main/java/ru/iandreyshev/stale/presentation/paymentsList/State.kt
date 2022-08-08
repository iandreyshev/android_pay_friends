package ru.iandreyshev.stale.presentation.paymentsList

import ru.iandreyshev.stale.domain.payment.Payment

data class State(
    val payments: List<Payment>,
    val isLoading: Boolean
) {

    companion object {
        fun default() = State(
            payments = listOf(),
            isLoading = true
        )
    }

}
