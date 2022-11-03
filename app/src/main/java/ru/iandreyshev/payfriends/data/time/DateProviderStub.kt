package ru.iandreyshev.payfriends.data.time

import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.payfriends.domain.time.Date
import ru.iandreyshev.payfriends.domain.time.DateProvider
import javax.inject.Inject

class DateProviderStub
@Inject constructor() : DateProvider {
    override fun currentDate(): Date = Date(ZonedDateTime.now())
}
