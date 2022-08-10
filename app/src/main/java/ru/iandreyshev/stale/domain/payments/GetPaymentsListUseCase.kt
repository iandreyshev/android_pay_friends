package ru.iandreyshev.stale.domain.payments

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPaymentsListUseCase(
    private val storage: PaymentsStorage
) {

    operator fun invoke(): Flow<List<PaymentSummary>> {
        return storage.observe()
            .map { list ->
                list.map { payment ->
                    PaymentSummary(
                        id = payment.id,
                        name = payment.name
                    )
                }
            }
    }

}
