package ru.iandreyshev.stale

import android.app.Application
import ru.iandreyshev.stale.data.payment.InMemoryPaymentsStorage
import ru.iandreyshev.stale.data.time.DateProviderStub
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
