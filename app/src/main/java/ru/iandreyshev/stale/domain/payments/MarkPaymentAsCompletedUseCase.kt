package ru.iandreyshev.stale.domain.payments

import ru.iandreyshev.stale.domain.core.PaymentId

class MarkPaymentAsCompletedUseCase(
    private val storage: PaymentsStorage
) {

    suspend operator fun invoke(id: PaymentId, isCompleted: Boolean): Boolean {
        val payment = storage.get(id) ?: return false
        storage.save(payment.copy(isCompleted = isCompleted))
        return true
    }

}
