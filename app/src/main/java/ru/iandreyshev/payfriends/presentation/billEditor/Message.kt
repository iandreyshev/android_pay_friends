package ru.iandreyshev.payfriends.presentation.billEditor

import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.Payment

sealed interface Message {
    data class Started(val startedState: State) : Message
    data class UpdateProducer(val producer: Member?, val receiverSuggestions: List<Member>) : Message
    data class UpdateProducerSuggestions(
        val candidateQuery: String,
        val candidate: String,
        val suggestions: List<Member>,
    ) : Message

    data class ChangeTitle(val title: String) : Message

    data class UpdateReceiverSuggestions(
        val candidateQuery: String,
        val candidate: String,
        val suggestions: List<Member>,
    ) : Message

    data class UpdatePayments(val payments: List<Payment>, val totalCost: Int) : Message
    data class ChangeSavingState(val isSaving: Boolean) : Message

    data class UpdateCommonBill(val state: CommonBillState) : Message
}
