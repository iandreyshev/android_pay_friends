package ru.iandreyshev.payfriends.domain.paymentEditor

import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.Payment
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.core.Result
import ru.iandreyshev.payfriends.domain.payments.PaymentsStorage
import ru.iandreyshev.payfriends.domain.time.DateProvider

class SavePaymentUseCase(
    private val isDraftValid: ValidatePaymentDraftUseCase,
    private val storage: PaymentsStorage,
    private val dateProvider: DateProvider
) {

    suspend operator fun invoke(draft: PaymentDraft): Result<Payment> {
        val preparedDraft = draft.copy(name = draft.name.trim())
        val validationErrors = isDraftValid(preparedDraft)

        if (validationErrors.isNotEmpty()) {
            return Result.Error(ErrorType.InvalidPaymentDraft(validationErrors))
        }

        var payment = Payment(
            id = draft.id ?: PaymentId(""),
            name = draft.name,
            members = draft.members,
            transactions = listOf(),
            isCompleted = false,
            creationDate = dateProvider.currentDate()
        )
        payment = storage.save(payment)

        return Result.Success(payment)
    }

}
