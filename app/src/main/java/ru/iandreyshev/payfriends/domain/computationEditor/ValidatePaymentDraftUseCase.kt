package ru.iandreyshev.payfriends.domain.computationEditor

import javax.inject.Inject

class ValidatePaymentDraftUseCase
@Inject constructor(
    private val isMemberValid: ValidateMemberUseCase
) {

    operator fun invoke(draft: ComputationDraft): List<PaymentDraftError> {
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
