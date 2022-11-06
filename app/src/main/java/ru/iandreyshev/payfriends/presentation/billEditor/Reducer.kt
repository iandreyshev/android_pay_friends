package ru.iandreyshev.payfriends.presentation.billEditor

import com.arkivanov.mvikotlin.core.store.Reducer

val Reducer = Reducer<State, Message> { message ->
    when (message) {
        is Message.Started -> message.startedState
        is Message.UpdateProducerSuggestions -> copy(
            backerField = backerField.copy(
                candidateQuery = message.candidateQuery,
                candidate = message.candidate,
                suggestions = message.suggestions,
            )
        )
        is Message.ChangeTitle -> copy(title = message.title)
        is Message.UpdateBacker -> copy(
            backerField = backerField.copy(backer = message.backer),
            receiverField = receiverField.copy(suggestions = message.receiverSuggestions),
        )
        is Message.UpdateReceiverSuggestions -> copy(
            receiverField = receiverField.copy(
                candidateQuery = message.candidateQuery,
                candidate = message.candidate,
                suggestions = message.suggestions,
            )
        )
        is Message.UpdatePayments -> copy(
            payments = message.payments,
            backerField = backerField.copy(cost = message.totalCost)
        )
        is Message.ChangeSavingState -> copy(isSaving = message.isSaving)
    }
}
