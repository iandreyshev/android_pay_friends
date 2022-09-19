package ru.iandreyshev.payfriends.data.realm

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.*
import ru.iandreyshev.payfriends.domain.time.Date
import ru.iandreyshev.payfriends.system.Dispatchers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealmStorage
@Inject constructor(
    private val config: RealmConfiguration,
    private val dispatchers: Dispatchers
) : Storage {

    private val realm: Realm by lazy {
        Realm.open(configuration = config)
    }

    override suspend fun save(computation: Computation): Computation =
        withContext(dispatchers.io) {
            realm.write {
                val existedEntity = realm.query<ComputationEntity>(query = "id == $0", computation.id.value)
                    .find()
                    .firstOrNull()

                if (existedEntity != null) {
                    removeImpl(computation.id)
                    return@write findLatest(existedEntity)
                        ?.also {
                            it.title = computation.title
                            it.creationDate = computation.creationDate.asStorageModel()
                            it.isCompleted = computation.isCompleted
                            it.members = computation.members
                                .map { member ->
                                    member.asStorageModel()
                                        .apply { id = newId() }
                                }
                                .toRealmList()
                        }
                        ?: throw IllegalStateException("Entity not found")
                }

                copyToRealm(ComputationEntity().also { newEntity ->
                    newEntity.id = newId()
                    newEntity.title = computation.title
                    newEntity.creationDate = computation.creationDate.asStorageModel()
                    newEntity.isCompleted = computation.isCompleted
                    newEntity.members = computation.members
                        .map { member ->
                            member.asStorageModel()
                                .apply { id = newId() }
                        }
                        .toRealmList()
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
            realm.write { removeImpl(id) }
        }
    }

    override fun observable(): Flow<List<Computation>> =
        realm.query<ComputationEntity>()
            .asFlow()
            .map { change -> change.list.map { it.asDomainModel() } }

    private fun MutableRealm.removeImpl(id: ComputationId) {
        val entity = realm.query<ComputationEntity>(query = "id = $0", id.value)
            .find()
            .first()

        entity.bills.forEach {
            findLatest(it)?.let { latest -> delete(latest) }
        }

        entity.members.forEach {
            findLatest(it)?.let { latest -> delete(latest) }
        }

        findLatest(entity)?.let { latest -> delete(latest) }
    }

    private fun ComputationEntity.asDomainModel() =
        Computation(
            id = ComputationId(id),
            title = title,
            bills = bills.map { it.asDomainModel() },
            members = members.map { it.asDomainModel() },
            creationDate = creationDate.asDomainModel(),
            isCompleted = isCompleted
        )

    private fun BillEntity.asDomainModel() =
        Bill(
            id = BillId(id),
            title = title,
            backer = backer?.asDomainModel() ?: Member(""),
            payments = payments.map { it.asDomainModel() },
            creationDate = creationDate.asDomainModel()
        )

    private fun PaymentEntity.asDomainModel() = Payment(
        id = PaymentId(id),
        receiver = receiver?.asDomainModel() ?: Member(""),
        cost = cost,
        description = description,
        creationDate = creationDate.asDomainModel()
    )

    private fun Member.asStorageModel() = MemberEntity(name = name)

    private fun MemberEntity.asDomainModel() = Member(name = name)

    private fun Date.asStorageModel() = value

    private fun String.asDomainModel() = Date("")

    private fun newId() = UUID.randomUUID().toString()

}
