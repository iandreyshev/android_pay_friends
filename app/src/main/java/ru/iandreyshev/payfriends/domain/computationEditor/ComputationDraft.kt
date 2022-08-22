package ru.iandreyshev.payfriends.domain.computationEditor

import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.ComputationId

data class ComputationDraft(
    val id: ComputationId?,
    val name: String,
    val members: List<Member>
) {

    companion object {
        fun empty() = ComputationDraft(
            id = ComputationId(""),
            name = "",
            members = listOf()
        )
    }

}
