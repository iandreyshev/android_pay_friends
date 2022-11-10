package ru.iandreyshev.payfriends.domain.settings

import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.system.Dispatchers
import javax.inject.Inject

class ImportComputationsUseCase
@Inject constructor(
    private val storage: Storage,
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(computations: ImportedComputations) {
        withContext(dispatchers.io) {
            storage.import(computations)
        }
    }

}
