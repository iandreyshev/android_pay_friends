package ru.iandreyshev.stale.ui.transactionEditor.items

import ru.iandreyshev.stale.domain.core.Member

data class ReceiverFieldItem(
    val receiver: Member?,
    val suggestions: List<Member>,
    val candidateQuery: String,
    val candidate: String
) {

    val hasSuggestions: Boolean
        get() = suggestions.isNotEmpty()

}
