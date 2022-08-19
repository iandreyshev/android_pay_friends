package ru.iandreyshev.payfriends.presentation.paymentEditor

import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.paymentEditor.PaymentDraft
import ru.iandreyshev.payfriends.domain.paymentEditor.PaymentDraftError

data class State(
    val draft: PaymentDraft,
    val memberCandidate: String,
    val errors: List<PaymentDraftError>,
    val isLoading: Boolean,
    val isSavingInProgress: Boolean
) {

    val canAddMember = memberCandidate.isNotEmpty()

    companion object {
        fun default(id: PaymentId? = null) = State(
            draft = PaymentDraft(
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
