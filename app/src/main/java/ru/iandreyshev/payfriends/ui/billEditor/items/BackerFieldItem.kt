package ru.iandreyshev.payfriends.ui.billEditor.items

import ru.iandreyshev.payfriends.domain.core.Member

data class BackerFieldItem(
    val backer: Member?,
    val cost: Int,
    val suggestions: List<Member>,
    val candidateQuery: String,
    val candidate: String,
    val isPaymentsEmptyViewVisible: Boolean
) {

    val hasSuggestions: Boolean
        get() = suggestions.isNotEmpty()

}
