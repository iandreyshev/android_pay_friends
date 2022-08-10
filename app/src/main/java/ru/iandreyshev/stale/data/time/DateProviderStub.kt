package ru.iandreyshev.stale.data.time

import ru.iandreyshev.stale.domain.time.DateProvider

class DateProviderStub : DateProvider {
    override fun currentDate(): String = "Date from DateProviderStub"
}
