package ru.iandreyshev.payfriends.domain.billEditor

import ru.iandreyshev.payfriends.domain.core.Member

class FilterMembers {

    data class Filters(
        val query: String = ""
    )

    operator fun invoke(members: List<Member>, filters: Filters): List<Member> {
        return members.filter {
            val lowercaseQuery = filters.query.lowercase()
            it.name.lowercase().contains(lowercaseQuery)
        }
    }

}
