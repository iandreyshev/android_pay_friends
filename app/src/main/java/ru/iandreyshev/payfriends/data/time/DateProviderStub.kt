package ru.iandreyshev.payfriends.data.time

import ru.iandreyshev.payfriends.domain.time.Date
import ru.iandreyshev.payfriends.domain.time.DateProvider

class DateProviderStub : DateProvider {
    override fun currentDate(): Date = Date("Date from DateProviderStub")
}
