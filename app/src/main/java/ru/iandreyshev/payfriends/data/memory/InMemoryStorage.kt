package ru.iandreyshev.payfriends.data.memory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.Computation
import ru.iandreyshev.payfriends.domain.core.ComputationId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryStorage
@Inject constructor() : Storage {

    private val mCache: MutableStateFlow<List<Computation>> = MutableStateFlow(createMock1())

    override suspend fun save(computation: Computation): Computation {
        val ids = mCache.value
            .map { it.id }
            .toHashSet()

        var newId: ComputationId

        if (computation.id in ids) {
            remove(computation.id)
            newId = computation.id
        } else do {
            newId = randomComputationId()
        } while (newId in ids)

        val newComputation = computation.copy(
            id = newId,
            bills = computation.bills
                .map { bill ->
                    bill.copy(
                        id = randomBillId(),
                        payments = bill.payments.map { payment ->
                            payment.copy(id = randomPaymentId())
                        })
                }
        )
        mCache.emit(mCache.value + newComputation)

        return newComputation
    }

    override suspend fun get(id: ComputationId): Computation? =
        mCache.value.firstOrNull { it.id == id }

    override suspend fun remove(id: ComputationId) {
        val newList = mCache.value.toMutableList()
        newList.removeIf { it.id == id }
        mCache.emit(newList)
    }

    override fun observable(): Flow<List<Computation>> = mCache

}
