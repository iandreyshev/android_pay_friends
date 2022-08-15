package ru.iandreyshev.stale.presentation.transactionEditor

import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.Transaction
import ru.iandreyshev.stale.domain.core.TransactionId

data class State(
    val paymentId: PaymentId,
    val transactionId: TransactionId,
    val producer: Member?,
    val producerSearchQuery: String,
    val producerSuggestions: List<Member>,
    val isAddingNewMemberActive: Boolean,
    val totalCost: Int,
    val members: List<Member>,
    val transactions: List<Transaction>,
    val isNewTransactionsAvailable: Boolean,
    val newTransactionReceiverSearchQuery: String,
    val newTransactionReceiverSuggestions: List<Member>,
) {

    companion object {
        fun default() = State(
            paymentId = PaymentId(""),
            transactionId = TransactionId(""),
            producer = null,
            producerSearchQuery = "",
            producerSuggestions = listOf(),
            isAddingNewMemberActive = false,
            totalCost = 0,
            members = listOf(),
            transactions = listOf(),
            isNewTransactionsAvailable = true,
            newTransactionReceiverSearchQuery = "",
            newTransactionReceiverSuggestions = listOf()
        )
    }

}
