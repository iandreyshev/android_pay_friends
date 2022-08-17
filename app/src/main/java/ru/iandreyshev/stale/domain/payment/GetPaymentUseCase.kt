package ru.iandreyshev.stale.domain.payment

import kotlinx.coroutines.withContext
import ru.iandreyshev.stale.domain.core.Payment
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.payments.PaymentsStorage
import ru.iandreyshev.stale.system.Dispatchers

class GetPaymentUseCase(
    private val storage: PaymentsStorage,
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(id: PaymentId): Payment? {
        return withContext(dispatchers.io) {
            storage.get(id)
        }
    }

}
