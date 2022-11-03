package ru.iandreyshev.payfriends

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.iandreyshev.payfriends.di.AppComponent
import ru.iandreyshev.payfriends.di.AppModule
import ru.iandreyshev.payfriends.di.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)

        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        lateinit var component: AppComponent
    }

}
