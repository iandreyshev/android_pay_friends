package ru.iandreyshev.stale.domain.payments

import kotlinx.coroutines.flow.Flow
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.Payment
import ru.iandreyshev.stale.domain.core.PaymentId

interface PaymentsStorage {
    suspend fun save(payment: Payment): Payment
    suspend fun get(id: PaymentId): Payment?
    suspend fun getMembers(id: PaymentId): List<Member>
    suspend fun remove(id: PaymentId)
    fun observe(): Flow<List<Payment>>
}
