package ru.iandreyshev.payfriends.ui.billEditor.items

import ru.iandreyshev.payfriends.domain.core.Member

data class ReceiverFieldItem(
    val suggestions: List<Member>,
    val candidateQuery: String,
    val candidate: String
) {

    val hasSuggestions: Boolean
        get() = suggestions.isNotEmpty()

}
