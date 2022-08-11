package ru.iandreyshev.stale.presentation.transactionEditor

import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.Transaction

data class State(
    val members: List<Member>,
    val selectedMember: Member?,
    val totalCost: Int,
    val transactions: List<Transaction>,
    val canAddMultipleTransactions: Boolean
) {

    companion object {
        fun default() = State(
            members = emptyList(),
            selectedMember = null,
            totalCost = 0,
            transactions = emptyList(),
            canAddMultipleTransactions = true
        )
    }

}
