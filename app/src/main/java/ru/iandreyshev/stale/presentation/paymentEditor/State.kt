package ru.iandreyshev.stale.presentation.paymentEditor

import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.paymentEditor.PaymentDraft
import ru.iandreyshev.stale.domain.paymentEditor.PaymentDraftError

data class State(
    val draft: PaymentDraft,
    val errors: List<PaymentDraftError>,
    val isLoading: Boolean,
    val isSavingInProgress: Boolean
) {

    companion object {
        fun default(id: PaymentId? = null) = State(
            draft = PaymentDraft(
                id = id,
                name = "",
                members = emptyList()
            ),
            errors = emptyList(),
            isLoading = true,
            isSavingInProgress = true
        )
    }

}
