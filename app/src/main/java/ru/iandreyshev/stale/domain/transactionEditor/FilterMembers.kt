package ru.iandreyshev.stale.domain.transactionEditor

import ru.iandreyshev.stale.domain.core.Member

class FilterMembers {

    data class Filters(
        val query: String = "",
        val exclude: List<String> = listOf()
    )

    operator fun invoke(members: List<Member>, filters: Filters): List<Member> {
        return listOf()
    }

}
