package ru.iandreyshev.payfriends

import android.app.Application
import ru.iandreyshev.payfriends.data.computation.InMemoryStorage
import ru.iandreyshev.payfriends.data.time.DateProviderStub
import ru.iandreyshev.payfriends.di.AppComponent
import ru.iandreyshev.payfriends.di.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        val component: AppComponent = DaggerAppComponent.create()
        val dateProvider = DateProviderStub()
    }

}
