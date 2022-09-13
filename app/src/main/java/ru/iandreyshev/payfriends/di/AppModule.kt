package ru.iandreyshev.payfriends.di

import dagger.Module
import dagger.Provides
import io.realm.kotlin.RealmConfiguration
import ru.iandreyshev.payfriends.data.realm.BillEntity
import ru.iandreyshev.payfriends.data.realm.ComputationEntity
import ru.iandreyshev.payfriends.data.realm.MemberEntity
import ru.iandreyshev.payfriends.data.realm.PaymentEntity
import ru.iandreyshev.payfriends.system.AppDispatchers
import ru.iandreyshev.payfriends.system.Dispatchers

@Module
class AppModule {

    @Provides
    fun provideDispatchers(): Dispatchers = AppDispatchers

    @Provides
    fun provideRealmConfiguration(): RealmConfiguration =
        RealmConfiguration.Builder(
            schema = setOf(
                ComputationEntity::class,
                BillEntity::class,
                PaymentEntity::class,
                MemberEntity::class
            )
        ).build()

}
