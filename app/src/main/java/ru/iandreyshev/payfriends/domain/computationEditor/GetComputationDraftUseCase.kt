package ru.iandreyshev.payfriends.domain.computationEditor

import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.computationsList.Storage

class GetComputationDraftUseCase(
    private val storage: Storage
) {

    suspend operator fun invoke(id: ComputationId?): ComputationDraft? {
        id ?: return ComputationDraft.empty()
        val computation = storage.get(id) ?: return null

        return ComputationDraft(
            id = computation.id,
            name = computation.title,
            members = computation.members
        )
    }

}
