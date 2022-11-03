package ru.iandreyshev.payfriends.ui.utils

import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.payfriends.domain.time.Date

object DateFormatter {

    private val formatter1 = DateTimeFormatter.ofPattern("dd MMMM HH:mm")
    private val formatter2 = DateTimeFormatter.ofPattern("dd MMMM YYYY")

    // 10 December 14:42
    fun format1(date: Date): String {
        return date.value.format(formatter1)
    }

    // 10 December 1996
    fun format2(date: Date): String {
        return date.value.format(formatter2)
    }

}
