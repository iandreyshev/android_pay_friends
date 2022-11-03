package ru.iandreyshev.payfriends.data.storage.realm

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.*
import ru.iandreyshev.payfriends.domain.time.Date
import ru.iandreyshev.payfriends.system.Dispatchers
import java.lang.IllegalStateException
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

            return@withContext realm.write {
                val existedEntity: ComputationEntity? =
                    findLatest((getImpl(computation.id) ?: throw IllegalStateException("Entity not found")))

                if (existedEntity != null) {
                    existedEntity.title = computation.title
                    //existedEntity.creationDate = computation.creationDate.asStorageModel()
                    existedEntity.isCompleted = computation.isCompleted
                    existedEntity.members = computation.members
                        .map { member ->
                            member.asStorageModel()
                                .apply { id = newId() }
                        }
                        .toRealmList()

                    removeImpl(computation.id)
                    copyToRealm(existedEntity)
                }

                val newEntity = ComputationEntity()
                newEntity.id = newId()
                newEntity.title = computation.title
                //newEntity.creationDate = computation.creationDate.asStorageModel()
                newEntity.isCompleted = computation.isCompleted
                newEntity.members = computation.members
                    .map { member ->
                        member.asStorageModel()
                            .apply { id = newId() }
                    }
                    .toRealmList()

                copyToRealm(newEntity)
            }.asDomainModel()
        }

    override suspend fun get(id: ComputationId): Computation? =
        withContext(dispatchers.io) {
            getImpl(id)?.asDomainModel()
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

    private fun getImpl(id: ComputationId): ComputationEntity? =
        realm.query<ComputationEntity>("id == $0", id.value)
            .find()
            .firstOrNull()

    private fun MutableRealm.removeImpl(id: ComputationId) {
        val entity = query<ComputationEntity>("id == $0", id.value)
            .find()
            .first()

        entity.bills.forEach { bill ->
            delete(bill.payments)
        }
        delete(entity.bills)
        delete(entity.members)
        delete(entity)
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

    private fun String.asDomainModel() = Date(ZonedDateTime.now())

    private fun newId() = UUID.randomUUID().toString()

}
