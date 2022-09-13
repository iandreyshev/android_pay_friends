package ru.iandreyshev.payfriends.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.Computation
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.time.Date
import ru.iandreyshev.payfriends.system.Dispatchers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealmStorage
@Inject constructor(
    private val dispatchers: Dispatchers
) : Storage {

    private val realm: Realm by lazy {
        Realm.open(
            configuration = RealmConfiguration.Builder(
                schema = setOf(
                    ComputationEntity::class,
                    BillEntity::class,
                    PaymentEntity::class,
                    MemberEntity::class
                )
            ).build()
        )
    }

    override suspend fun save(computation: Computation): Computation =
        withContext(dispatchers.io) {
            realm.write {
                val existedEntity = realm.query<ComputationEntity>(query = "id == $0", computation.id.value)
                    .find()
                    .firstOrNull()

                if (existedEntity != null) {
                    return@write findLatest(existedEntity)
                        ?.also {
                            it.title = computation.title
                            it.creationDate = computation.creationDate.asStorageModel()
                            it.isCompleted = computation.isCompleted
                        }
                        ?: throw IllegalStateException("Entity not found")
                }

                copyToRealm(ComputationEntity().also { newEntity ->
                    newEntity.id = newId()
                    newEntity.title = computation.title
                    newEntity.creationDate = computation.creationDate.asStorageModel()
                    newEntity.isCompleted = computation.isCompleted
                })
            }.asDomainModel()
        }

    override suspend fun get(id: ComputationId): Computation? =
        withContext(dispatchers.io) {
            realm.query<ComputationEntity>(query = "id = $0", id.value)
                .find()
                .firstOrNull()
                ?.asDomainModel()
        }

    override suspend fun remove(id: ComputationId) {
        withContext(dispatchers.io) {
            realm.write {
                val entity = realm.query<ComputationEntity>(query = "id = $0", id.value)
                    .find()
                    .first()

                findLatest(entity)?.let { delete(it) }
            }
        }
    }

    override fun observable(): Flow<List<Computation>> =
        realm.query<ComputationEntity>()
            .asFlow()
            .map { change -> change.list.map { it.asDomainModel() } }

    private fun ComputationEntity.asDomainModel() =
        Computation(
            id = ComputationId(id),
            title = title,
            bills = listOf(),
            members = listOf(),
            creationDate = Date(""),
            isCompleted = isCompleted
        )

    private fun Date.asStorageModel() = Date()

    private fun newId() = UUID.randomUUID().toString()

}
