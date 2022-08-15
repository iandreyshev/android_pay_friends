package ru.iandreyshev.stale.presentation.transactionEditor

import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.Transaction

data class State(
    val paymentId: PaymentId,
    val producer: Member?,
    val producerSearchQuery: String,
    val producerSuggestions: List<Member>,
    val canAddNewMember: Boolean,
    val selectedMember: Member?,
    val totalCost: Int,
    val members: List<Member>,
    val transactions: List<Transaction>,
    val isNewTransactionsAvailable: Boolean,
    val newTransactionReceiverSearchQuery: String,
    val newTransactionReceiverSuggestions: List<Member>,
) {

    companion object {
        fun default(paymentId: PaymentId = PaymentId("")) = State(
            paymentId = paymentId,
            producer = null,
            producerSearchQuery = "",
            producerSuggestions = listOf(),
            canAddNewMember = false,
            selectedMember = null,
            totalCost = 0,
            members = listOf(),
            transactions = listOf(),
            isNewTransactionsAvailable = true,
            newTransactionReceiverSearchQuery = "",
            newTransactionReceiverSuggestions = listOf()
        )
    }

}
