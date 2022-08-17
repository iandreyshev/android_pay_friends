package ru.iandreyshev.stale.presentation.payment

import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.Transaction

data class State(
    val id: PaymentId,
    val name: String,
    val creationDate: String,
    val result: List<Transaction>,
    val history: List<Transaction>,
    val isLoading: Boolean
) {

    companion object {
        fun default() = State(
            id = PaymentId(""),
            name = "",
            creationDate = "",
            result = emptyList(),
            history = emptyList(),
            isLoading = true
        )
    }

}
