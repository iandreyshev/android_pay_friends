package ru.iandreyshev.payfriends.di

import dagger.Binds
import dagger.Module
import ru.iandreyshev.payfriends.data.storage.memory.InMemoryStorage
import ru.iandreyshev.payfriends.data.storage.realm.RealmStorage
import ru.iandreyshev.payfriends.data.time.DateProviderStub
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.time.DateProvider

@Module
interface AppModuleBindings {

    @Binds
    fun bindStorage(storage: InMemoryStorage): Storage

    @Binds
    fun bindDateProvider(dateProvider: DateProviderStub): DateProvider

}
