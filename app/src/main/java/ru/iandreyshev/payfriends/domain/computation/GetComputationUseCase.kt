package ru.iandreyshev.payfriends.domain.computation

import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.Computation
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.system.Dispatchers
import javax.inject.Inject

class GetComputationUseCase
@Inject constructor(
    private val storage: Storage,
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(id: ComputationId): Computation? {
        return withContext(dispatchers.io) {
            storage.get(id)
        }
    }

}
