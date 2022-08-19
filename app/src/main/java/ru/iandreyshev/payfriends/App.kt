package ru.iandreyshev.payfriends

import android.app.Application
import ru.iandreyshev.payfriends.data.payment.InMemoryPaymentsStorage
import ru.iandreyshev.payfriends.data.time.DateProviderStub
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        val storage = InMemoryPaymentsStorage()
        val dateProvider = DateProviderStub()
    }

}
