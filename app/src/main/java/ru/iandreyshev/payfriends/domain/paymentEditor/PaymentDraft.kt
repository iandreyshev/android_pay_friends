package ru.iandreyshev.payfriends.domain.paymentEditor

import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.PaymentId

data class PaymentDraft(
    val id: PaymentId?,
    val name: String,
    val members: List<Member>
) {

    companion object {
        fun empty() = PaymentDraft(
            id = PaymentId(""),
            name = "",
            members = listOf()
        )
    }

}
