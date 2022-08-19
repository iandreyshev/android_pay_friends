package ru.iandreyshev.payfriends.presentation.payment

import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.core.Transaction
import ru.iandreyshev.payfriends.domain.time.Date

data class State(
    val id: PaymentId,
    val name: String,
    val creationDate: Date,
    val result: List<Transaction>,
    val history: List<Transaction>,
    val isLoading: Boolean
) {

    companion object {
        fun default() = State(
            id = PaymentId(""),
            name = "",
            creationDate = Date(""),
            result = emptyList(),
            history = emptyList(),
            isLoading = true
        )
    }

}
