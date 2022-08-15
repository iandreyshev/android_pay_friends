package ru.iandreyshev.stale.data.payment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.iandreyshev.stale.domain.core.Member
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

    override suspend fun getMembers(id: PaymentId): List<Member> {
        TODO("getMembers not implemented")
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
