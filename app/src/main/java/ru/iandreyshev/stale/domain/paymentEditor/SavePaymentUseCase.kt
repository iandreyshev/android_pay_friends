package ru.iandreyshev.stale.domain.paymentEditor

import ru.iandreyshev.stale.domain.core.ErrorType
import ru.iandreyshev.stale.domain.core.Payment
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.Result
import ru.iandreyshev.stale.domain.payments.PaymentsStorage
import ru.iandreyshev.stale.domain.time.DateProvider

class SavePaymentUseCase(
    private val validate: ValidatePaymentDraftUseCase,
    private val storage: PaymentsStorage,
    private val dateProvider: DateProvider
) {

    suspend operator fun invoke(draft: PaymentDraft): Result<PaymentId> {
        val preparedDraft = draft.copy(name = draft.name.trim())
        val validationErrors = validate(preparedDraft)

        if (validationErrors.isNotEmpty()) {
            return Result.Error(ErrorType.InvalidPaymentDraft(validationErrors))
        }

        var payment = Payment(
            id = draft.id ?: PaymentId(""),
            name = draft.name,
            members = draft.members,
            isArchived = false,
            creationDate = dateProvider.currentDate()
        )
        payment = storage.save(payment)

        return Result.Success(payment.id)
    }

}
