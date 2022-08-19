package ru.iandreyshev.payfriends.domain.payment

import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.core.Payment
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.payments.PaymentsStorage
import ru.iandreyshev.payfriends.system.Dispatchers

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
