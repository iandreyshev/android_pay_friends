package ru.iandreyshev.stale.domain.paymentEditor

import ru.iandreyshev.stale.domain.core.Member

class ValidateMemberUseCase {

    operator fun invoke(member: Member): Boolean =
        member.name.isNotBlank()

}
