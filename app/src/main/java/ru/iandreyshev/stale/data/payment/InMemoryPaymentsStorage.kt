package ru.iandreyshev.stale.data.payment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.iandreyshev.stale.domain.core.Payment
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.payments.PaymentsStorage
import kotlin.random.Random

class InMemoryPaymentsStorage : PaymentsStorage {

    private val mCache: MutableStateFlow<List<Payment>> = MutableStateFlow(listOf())

    override suspend fun save(payment: Payment): Payment {
        val ids = mCache.value
            .map { it.id }
            .toHashSet()

        if (payment.id in ids) {
            remove(payment.id)
        }

        var newId: PaymentId
        do {
            newId = PaymentId(Random.nextInt().toString())
        } while (newId in ids)

        val newPayment = payment.copy(id = newId)
        mCache.emit(mCache.value + newPayment)
        return newPayment
    }

    override suspend fun get(id: PaymentId): Payment? {
        return mCache.value.firstOrNull { it.id == id }
    }

    override suspend fun remove(id: PaymentId) {
        mCache.value.toMutableList()
            .removeIf { it.id == id }
    }

    override fun observe(): Flow<List<Payment>> {
        return mCache
    }

}
