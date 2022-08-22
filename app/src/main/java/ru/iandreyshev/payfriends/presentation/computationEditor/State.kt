package ru.iandreyshev.payfriends.presentation.computationEditor

import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.computationEditor.ComputationDraft
import ru.iandreyshev.payfriends.domain.computationEditor.PaymentDraftError

data class State(
    val draft: ComputationDraft,
    val memberCandidate: String,
    val errors: List<PaymentDraftError>,
    val isLoading: Boolean,
    val isSavingInProgress: Boolean
) {

    val canAddMember = memberCandidate.isNotEmpty()

    companion object {
        fun default(id: ComputationId? = null) = State(
            draft = ComputationDraft(
                id = id,
                name = "",
                members = emptyList()
            ),
            memberCandidate = "",
            errors = emptyList(),
            isLoading = true,
            isSavingInProgress = true
        )
    }

}
