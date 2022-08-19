package ru.iandreyshev.stale.presentation.transactionEditor

import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.Transaction

sealed interface Message {
    data class Started(val startedState: State) : Message
    data class UpdateProducer(val producer: Member?, val receiverSuggestions: List<Member>) : Message
    data class UpdateProducerSuggestions(
        val candidateQuery: String,
        val candidate: String,
        val suggestions: List<Member>,
    ) : Message

    data class UpdateReceiverSuggestions(
        val candidateQuery: String,
        val candidate: String,
        val suggestions: List<Member>,
    ) : Message

    data class UpdateTransactions(val transactions: List<Transaction>, val totalCost: Int) : Message
}
