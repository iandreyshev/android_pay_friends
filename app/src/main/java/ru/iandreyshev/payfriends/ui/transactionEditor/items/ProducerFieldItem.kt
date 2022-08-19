package ru.iandreyshev.payfriends.ui.transactionEditor.items

import ru.iandreyshev.payfriends.domain.core.Member

data class ProducerFieldItem(
    val producer: Member?,
    val cost: Int,
    val suggestions: List<Member>,
    val candidateQuery: String,
    val candidate: String,
    val isTransactionsEmptyViewVisible: Boolean
) {

    val hasSuggestions: Boolean
        get() = suggestions.isNotEmpty()

}
