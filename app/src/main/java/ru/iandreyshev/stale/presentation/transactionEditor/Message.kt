package ru.iandreyshev.stale.presentation.transactionEditor

import ru.iandreyshev.stale.domain.core.Member

sealed interface Message {
    data class Started(val startedState: State) : Message
    data class UpdateProducer(val producer: Member?) : Message
    data class UpdateProducerSuggestions(
        val suggestions: List<Member>,
        val candidate: String
    ) : Message
}
