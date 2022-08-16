package ru.iandreyshev.stale.presentation.transactionEditor

import com.arkivanov.mvikotlin.core.store.Reducer

val Reducer = Reducer<State, Message> { message ->
    when (message) {
        is Message.Started -> message.startedState
        is Message.UpdateProducerSuggestions ->
            copy(
                producerField = producerField.copy(
                    suggestions = message.suggestions,
                    candidate = message.candidate
                )
            )
        is Message.UpdateProducer ->
            copy(producerField = producerField.copy(producer = message.producer))
    }
}
