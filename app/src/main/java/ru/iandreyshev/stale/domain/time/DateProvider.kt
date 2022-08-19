package ru.iandreyshev.stale.domain.time

interface DateProvider {
    fun currentDate(): Date
}
