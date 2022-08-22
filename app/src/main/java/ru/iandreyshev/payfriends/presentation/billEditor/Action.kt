package ru.iandreyshev.payfriends.presentation.billEditor

import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.ComputationId

sealed interface Action {
    data class OnStart(
        val computationId: ComputationId,
        val billId: BillId?
    ) : Action
}
