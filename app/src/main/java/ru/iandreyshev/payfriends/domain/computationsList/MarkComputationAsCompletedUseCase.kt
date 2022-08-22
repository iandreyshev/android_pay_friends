package ru.iandreyshev.payfriends.domain.computationsList

import ru.iandreyshev.payfriends.domain.core.ComputationId

class MarkComputationAsCompletedUseCase(
    private val storage: Storage
) {

    suspend operator fun invoke(id: ComputationId, isCompleted: Boolean): Boolean {
        val computation = storage.get(id) ?: return false
        storage.save(computation.copy(isCompleted = isCompleted))
        return true
    }

}
