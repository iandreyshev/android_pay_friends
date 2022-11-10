package ru.iandreyshev.payfriends.domain.computationsList

import kotlinx.coroutines.flow.Flow
import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.Computation
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.settings.ImportedComputations

interface Storage {
    suspend fun save(computation: Computation): Computation
    suspend fun get(id: ComputationId): Computation?
    suspend fun remove(id: ComputationId)
    suspend fun remove(id: BillId)
    fun observable(): Flow<List<Computation>>
    suspend fun import(computations: ImportedComputations): Boolean
}
