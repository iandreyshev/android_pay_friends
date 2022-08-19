package ru.iandreyshev.payfriends.domain.time

interface DateProvider {
    fun currentDate(): Date
}
