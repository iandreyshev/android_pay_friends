package ru.iandreyshev.payfriends.di

import dagger.Module
import dagger.Provides
import ru.iandreyshev.payfriends.system.AppDispatchers
import ru.iandreyshev.payfriends.system.Dispatchers

@Module
class AppModule {

    @Provides
    fun provideDispatchers(): Dispatchers = AppDispatchers

}
