package ru.iandreyshev.payfriends.presentation.billEditor

import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.Payment

data class State(
    val computationId: ComputationId,
    val billId: BillId?,
    val number: Int,
    val title: String,
    val backerField: BackerFieldState,
    val receiverField: ReceiverFieldState,
    val payments: List<Payment>,
    val members: List<Member>,
    val isStarted: Boolean,
    val isSaving: Boolean
) {

    companion object {
        fun default() = State(
            computationId = ComputationId.none(),
            billId = null,
            title = "",
            number = 0,
            backerField = BackerFieldState.default(),
            members = listOf(),
            payments = listOf(),
            receiverField = ReceiverFieldState.default(),
            isStarted = false,
            isSaving = false
        )
    }

}

data class BackerFieldState(
    val backer: Member?,
    val suggestions: List<Member>,
    val candidateQuery: String,
    val candidate: String,
    val cost: Int,
) {

    companion object {
        fun default() = BackerFieldState(
            backer = null,
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
