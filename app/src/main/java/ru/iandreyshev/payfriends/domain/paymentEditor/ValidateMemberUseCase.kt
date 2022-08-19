package ru.iandreyshev.payfriends.domain.paymentEditor

import ru.iandreyshev.payfriends.domain.core.Member

class ValidateMemberUseCase {

    operator fun invoke(member: Member): Boolean =
        member.name.isNotBlank()

}
