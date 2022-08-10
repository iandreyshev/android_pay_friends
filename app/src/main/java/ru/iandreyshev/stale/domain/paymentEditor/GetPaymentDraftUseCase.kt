package ru.iandreyshev.stale.domain.paymentEditor

import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.payments.PaymentsStorage

class GetPaymentDraftUseCase(
    private val storage: PaymentsStorage
) {

    suspend operator fun invoke(id: PaymentId?): PaymentDraft? {
        id ?: return PaymentDraft.empty()
        val payment = storage.get(id) ?: return null

        return PaymentDraft(
            id = payment.id,
            name = payment.name,
            members = payment.members
        )
    }

}
