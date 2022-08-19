package ru.iandreyshev.payfriends.domain.payments

import kotlinx.coroutines.flow.Flow
import ru.iandreyshev.payfriends.domain.core.Payment
import ru.iandreyshev.payfriends.domain.core.PaymentId

interface PaymentsStorage {
    suspend fun save(payment: Payment): Payment
    suspend fun get(id: PaymentId): Payment?
    suspend fun remove(id: PaymentId)
    fun observe(): Flow<List<Payment>>
}
