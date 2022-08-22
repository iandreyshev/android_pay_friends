package ru.iandreyshev.payfriends.domain.billEditor

import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.Payment

data class BillDraft(
    val id: BillId,
    val title: String,
    val backer: Member?,
    val payments: List<Payment>,
)
