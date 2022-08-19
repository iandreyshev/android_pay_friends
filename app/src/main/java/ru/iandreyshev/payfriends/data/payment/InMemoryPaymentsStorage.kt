package ru.iandreyshev.payfriends.data.payment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.iandreyshev.payfriends.domain.core.Payment
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.payments.PaymentsStorage

class InMemoryPaymentsStorage : PaymentsStorage {

    private val mCache: MutableStateFlow<List<Payment>> = MutableStateFlow(createMock1())

    override suspend fun save(payment: Payment): Payment {
        val ids = mCache.value
            .map { it.id }
            .toHashSet()

        if (payment.id in ids) {
            remove(payment.id)
        }

        var newId: PaymentId
        do {
            newId = randomPaymentId()
        } while (newId in ids)

        val newPayment = payment.copy(id = newId)
        mCache.emit(mCache.value + newPayment)
        return newPayment
    }

    override suspend fun get(id: PaymentId): Payment? {
        return mCache.value.firstOrNull { it.id == id }
    }

    override suspend fun remove(id: PaymentId) {
        val newList = mCache.value.toMutableList()
        newList.removeIf { it.id == id }
        mCache.emit(newList)
    }

    override fun observe(): Flow<List<Payment>> {
        return mCache
    }

}
