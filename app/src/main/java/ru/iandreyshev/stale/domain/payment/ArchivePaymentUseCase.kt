package ru.iandreyshev.stale.domain.payment

class ArchivePaymentUseCase(
    private val storage: PaymentsStorage
) {

    suspend operator fun invoke(id: PaymentId, isArchived: Boolean): Boolean {
        val payment = storage.get(id) ?: return false
        storage.save(payment.copy(isArchived = isArchived))
        return true
    }

}
