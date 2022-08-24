package ru.iandreyshev.payfriends.domain.computationEditor

import ru.iandreyshev.payfriends.domain.core.Member
import javax.inject.Inject

class ValidateMemberUseCase
@Inject constructor() {

    operator fun invoke(member: Member): Boolean =
        member.name.isNotBlank()

}
