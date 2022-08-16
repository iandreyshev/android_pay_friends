package ru.iandreyshev.stale.presentation.transactionEditor

import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.Transaction
import ru.iandreyshev.stale.domain.core.TransactionId

data class State(
    val paymentId: PaymentId,
    val transactionId: TransactionId,
    val producerField: ProducerFieldState,
    val receiverField: ReceiverFieldState,
    val transactions: List<Transaction>,
    val members: List<Member>,
    val isStarted: Boolean
) {

    companion object {
        fun default() = State(
            paymentId = PaymentId(""),
            transactionId = TransactionId(""),
            producerField = ProducerFieldState.default(),
            members = listOf(),
            transactions = listOf(),
            receiverField = ReceiverFieldState.default(),
            isStarted = false
        )
    }

}

data class ProducerFieldState(
    val producer: Member?,
    val suggestions: List<Member>,
    val candidateQuery: String,
    val candidate: String,
    val cost: Int,
) {

    companion object {
        fun default() = ProducerFieldState(
            producer = null,
            candidateQuery = "",
            candidate = "",
            suggestions = listOf(),
            cost = 0,
        )
    }

}

data class ReceiverFieldState(
    val isEnabled: Boolean,
    val receiver: Member?,
    val suggestions: List<Member>,
    val isCandidateActive: Boolean,
    val candidateQuery: String,
    val candidate: String
) {

    companion object {
        fun default() = ReceiverFieldState(
            isEnabled = true,
            receiver = null,
            suggestions = listOf(),
            isCandidateActive = false,
            candidateQuery = "",
            candidate = "",
        )
    }

}
