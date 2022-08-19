package ru.iandreyshev.payfriends.domain.payments

import ru.iandreyshev.payfriends.domain.core.PaymentId

class MarkPaymentAsCompletedUseCase(
    private val storage: PaymentsStorage
) {

    suspend operator fun invoke(id: PaymentId, isCompleted: Boolean): Boolean {
        val payment = storage.get(id) ?: return false
        storage.save(payment.copy(isCompleted = isCompleted))
        return true
    }

}
