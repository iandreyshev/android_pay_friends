package ru.iandreyshev.stale.domain.paymentEditor

class ValidatePaymentDraftUseCase(
    private val isMemberValid: ValidateMemberUseCase
) {

    operator fun invoke(draft: PaymentDraft): List<PaymentDraftError> {
        val errors = mutableListOf<PaymentDraftError>()

        if (draft.name.isBlank()) {
            errors += PaymentDraftError.EMPTY_NAME
        }

        if (draft.members.any { !isMemberValid(it) }) {
            errors += PaymentDraftError.INVALID_MEMBER
        }

        return errors
    }

}
