package ru.iandreyshev.payfriends.ui.billEditor.items

import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.presentation.billEditor.CommonBillState

data class ProducerFieldItem(
    val producer: Member?,
    val cost: Int,
    val suggestions: List<Member>,
    val candidateQuery: String,
    val candidate: String,
    val isPaymentsEmptyViewVisible: Boolean,
    val commonBill: CommonBillState
) {

    val hasSuggestions: Boolean
        get() = suggestions.isNotEmpty()

}
