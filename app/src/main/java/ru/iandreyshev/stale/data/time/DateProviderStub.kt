package ru.iandreyshev.stale.data.time

import ru.iandreyshev.stale.domain.time.Date
import ru.iandreyshev.stale.domain.time.DateProvider

class DateProviderStub : DateProvider {
    override fun currentDate(): Date = Date("Date from DateProviderStub")
}
