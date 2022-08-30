package ru.iandreyshev.payfriends.data.realm

import kotlinx.coroutines.flow.Flow
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.Computation
import ru.iandreyshev.payfriends.domain.core.ComputationId

class RealmStorage : Storage {

    override suspend fun save(computation: Computation): Computation {
        TODO("save not implemented")
    }

    override suspend fun get(id: ComputationId): Computation? {
        TODO("get not implemented")
    }

    override suspend fun remove(id: ComputationId) {
        TODO("remove not implemented")
    }

    override fun observable(): Flow<List<Computation>> {
        TODO("observable not implemented")
    }

}
