package ru.iandreyshev.payfriends.domain.computationEditor

import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.Computation
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.Result
import ru.iandreyshev.payfriends.domain.time.DateProvider
import javax.inject.Inject

class SaveComputationUseCase
@Inject constructor(
    private val isDraftValid: ValidatePaymentDraftUseCase,
    private val storage: Storage,
    private val dateProvider: DateProvider
) {

    suspend operator fun invoke(draft: ComputationDraft): Result<Computation> {
        val preparedDraft = draft.copy(name = draft.name.trim())
        val validationErrors = isDraftValid(preparedDraft)

        if (validationErrors.isNotEmpty()) {
            return Result.Error(ErrorType.InvalidPaymentDraft(validationErrors))
        }

        var payment = Computation(
            id = draft.id ?: ComputationId(""),
            title = draft.name,
            members = draft.members,
            bills = listOf(),
            isCompleted = false,
            creationDate = dateProvider.currentDate()
        )
        payment = storage.save(payment)

        return Result.Success(payment)
    }

}
