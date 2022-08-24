package ru.iandreyshev.payfriends.di

import dagger.Component
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        AppModuleBindings::class,
        ViewModelsModule::class
    ]
)
@Singleton
interface AppComponent {
    val viewModelFactory: DaggerViewModelFactory
    val storage: Storage
}
