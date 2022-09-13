package ru.iandreyshev.payfriends.di

import dagger.Binds
import dagger.Module
import ru.iandreyshev.payfriends.data.realm.RealmStorage
import ru.iandreyshev.payfriends.data.time.DateProviderStub
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.time.DateProvider

@Module
interface AppModuleBindings {

    @Binds
    fun bindStorage(storage: RealmStorage): Storage

    @Binds
    fun bindDateProvider(dateProvider: DateProviderStub): DateProvider

}
