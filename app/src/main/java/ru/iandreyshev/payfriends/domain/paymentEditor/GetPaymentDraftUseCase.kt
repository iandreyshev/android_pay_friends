package ru.iandreyshev.payfriends.domain.paymentEditor

import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.payments.PaymentsStorage

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
