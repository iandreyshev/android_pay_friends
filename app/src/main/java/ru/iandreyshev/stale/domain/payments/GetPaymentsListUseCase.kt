package ru.iandreyshev.stale.domain.payments

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPaymentsListUseCase(
    private val storage: PaymentsStorage
) {

    data class Filter(
        val isListOfActivePayments: Boolean
    )

    operator fun invoke(filter: Filter): Flow<List<PaymentSummary>> {
        return storage.observe()
            .map { list ->
                list.filter { it.isCompleted != filter.isListOfActivePayments }
                    .map { payment ->
                        PaymentSummary(
                            id = payment.id,
                            name = payment.name
                        )
                    }
            }
    }

}
