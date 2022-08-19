package ru.iandreyshev.payfriends.presentation.transactionEditor

import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.core.Transaction
import ru.iandreyshev.payfriends.domain.core.TransactionId

data class State(
    val paymentId: PaymentId,
    val transactionId: TransactionId,
    val producerField: ProducerFieldState,
    val receiverField: ReceiverFieldState,
    val transactions: List<Transaction>,
    val members: List<Member>,
    val isStarted: Boolean,
    val isSaving: Boolean
) {

    companion object {
        fun default() = State(
            paymentId = PaymentId(""),
            transactionId = TransactionId.none(),
            producerField = ProducerFieldState.default(),
            members = listOf(),
            transactions = listOf(),
            receiverField = ReceiverFieldState.default(),
            isStarted = false,
            isSaving = false
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
    val suggestions: List<Member>,
    val isCandidateActive: Boolean,
    val candidateQuery: String,
    val candidate: String
) {

    companion object {
        fun default() = ReceiverFieldState(
            isEnabled = true,
            suggestions = listOf(),
            isCandidateActive = false,
            candidateQuery = "",
            candidate = "",
        )
    }

}
