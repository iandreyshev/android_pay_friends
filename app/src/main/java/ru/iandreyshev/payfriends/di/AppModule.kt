package ru.iandreyshev.payfriends.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import io.realm.kotlin.RealmConfiguration
import ru.iandreyshev.payfriends.data.storage.realm.BillEntity
import ru.iandreyshev.payfriends.data.storage.realm.ComputationEntity
import ru.iandreyshev.payfriends.data.storage.realm.MemberEntity
import ru.iandreyshev.payfriends.data.storage.realm.PaymentEntity
import ru.iandreyshev.payfriends.system.AppDispatchers
import ru.iandreyshev.payfriends.system.Dispatchers

@Module
class AppModule(
    private val context: Context
) {

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun provideResources(): Resources = context.resources

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
