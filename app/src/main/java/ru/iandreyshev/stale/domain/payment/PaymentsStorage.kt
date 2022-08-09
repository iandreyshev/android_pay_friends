package ru.iandreyshev.stale.domain.payment

import kotlinx.coroutines.flow.Flow

interface PaymentsStorage {
    suspend fun save(payment: Payment): Payment
    suspend fun get(id: PaymentId): Payment?
    suspend fun remove(id: PaymentId)
    fun observe(): Flow<List<Payment>>
}
