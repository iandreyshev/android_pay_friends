package ru.iandreyshev.payfriends.domain.computation

import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.system.Dispatchers
import javax.inject.Inject

class DeleteBillUseCase
@Inject constructor(
    private val storage: Storage,
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(id: BillId) {
        withContext(dispatchers.io) {
            storage.remove(id)
        }
    }

}
