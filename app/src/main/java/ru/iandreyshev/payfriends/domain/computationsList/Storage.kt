package ru.iandreyshev.payfriends.domain.computationsList

import kotlinx.coroutines.flow.Flow
import ru.iandreyshev.payfriends.domain.core.Computation
import ru.iandreyshev.payfriends.domain.core.ComputationId

interface Storage {
    suspend fun save(computation: Computation): Computation
    suspend fun get(id: ComputationId): Computation?
    suspend fun remove(id: ComputationId)
    fun observable(): Flow<List<Computation>>
}
