package ru.iandreyshev.stale.domain.paymentEditor

class ValidatePaymentDraftUseCase {

    operator fun invoke(draft: PaymentDraft): List<PaymentDraftError> {
        val errors = mutableListOf<PaymentDraftError>()

        if (draft.name.isBlank()) {
            errors += PaymentDraftError.EMPTY_NAME
        }

        return errors
    }

}
