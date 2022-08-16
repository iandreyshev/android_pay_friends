package ru.iandreyshev.stale.presentation.transactionEditor

import com.arkivanov.mvikotlin.core.store.Reducer

val Reducer = Reducer<State, Message> { message ->
    when (message) {
        is Message.Started -> message.startedState
        is Message.UpdateProducerSuggestions -> copy(
            producerField = producerField.copy(
                candidateQuery = message.candidateQuery,
                candidate = message.candidate,
                suggestions = message.suggestions,
            )
        )
        is Message.UpdateProducer -> when (message.producer) {
            null -> copy(producerField = producerField.copy(producer = message.producer))
            else -> copy(
                producerField = producerField.copy(producer = message.producer),
                transactions = transactions.map { transaction ->
                    transaction.copy(
                        participants = transaction.participants.copy(producer = message.producer)
                    )
                }
            )
        }
        is Message.UpdateReceiverSuggestions -> copy(
            receiverField = receiverField.copy(
                candidateQuery = message.candidateQuery,
                candidate = message.candidate,
                suggestions = message.suggestions,
            )
        )
        is Message.UpdateTransactions -> copy(transactions = message.transactions)
    }
}
