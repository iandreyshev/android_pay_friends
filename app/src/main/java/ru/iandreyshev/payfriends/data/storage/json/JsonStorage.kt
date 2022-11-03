package ru.iandreyshev.payfriends.data.storage.json

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.*
import ru.iandreyshev.payfriends.domain.time.Date
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonStorage
@Inject constructor(
    context: Context
) : Storage {

    private val memoryFile = File(context.filesDir, MEMORY_FILE_NAME)
    private val sharedFlow: MutableStateFlow<List<Computation>>
    private val dateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    init {
        if (!memoryFile.exists()) {
            memoryFile.setWritable(true)
            memoryFile.setReadable(true)
            memoryFile.createNewFile()
        }

        sharedFlow = MutableStateFlow(getComputations().asDomainModel())
    }

    override suspend fun save(computation: Computation): Computation {
        var newEntity = computation.asStorageComputation()

        if (newEntity.id == "") {
            newEntity = newEntity.copy(id = newUUID())
        }

        newEntity = newEntity.copy(bills = newEntity.bills.mapTo(mutableListOf()) { billJson ->
            val bill = when {
                billJson.id.isEmpty() -> billJson.copy(id = newUUID())
                else -> billJson
            }

            bill.copy(payments = bill.payments.mapTo(mutableListOf()) { paymentJson ->
                when {
                    paymentJson.id.isEmpty() -> paymentJson.copy(id = newUUID())
                    else -> paymentJson
                }
            })
        })

        val currEntitiesList = getComputations()
        currEntitiesList.removeAll { it.id == computation.id.value }
        currEntitiesList.add(newEntity)

        saveInMemory(currEntitiesList)

        return newEntity.asDomainModel()
    }

    override suspend fun get(id: ComputationId): Computation? {
        return getComputations()
            .asDomainModel()
            .firstOrNull { it.id == id }
    }

    override suspend fun remove(id: ComputationId) {
        val computations = getComputations()
        computations.removeAll { it.id == id.value }
        saveInMemory(computations)
    }

    override fun observable(): Flow<List<Computation>> = sharedFlow

    private fun saveInMemory(list: List<ComputationJson>) {
        memoryFile.writer().use {
            it.write("")
            it.write(Json.encodeToString(list))
        }

        sharedFlow.tryEmit(getComputations().asDomainModel())
    }

    private fun getComputations(): MutableList<ComputationJson> {
        if (!memoryFile.exists()) {
            return mutableListOf()
        }

        val text = memoryFile.reader().readText()

        if (text.isBlank()) {
            return mutableListOf()
        }

        return Json.decodeFromString(string = text)
    }

    private fun Computation.asStorageComputation() =
        ComputationJson(
            id = id.value,
            title = title,
            bills = bills.asStorageBillsList(),
            members = members.mapTo(mutableListOf()) { it.asStorageBillsList() },
            creationDate = creationDate.asStorageDate(),
            isCompleted = isCompleted
        )

    private fun List<Bill>.asStorageBillsList() = mapTo(mutableListOf()) {
        BillJson(
            id = it.id.value,
            title = it.title,
            backer = it.backer.asStorageBillsList(),
            payments = it.payments.asStoragePaymentsList(),
            creationDate = it.creationDate.asStorageDate()
        )
    }

    private fun List<Payment>.asStoragePaymentsList() = mapTo(mutableListOf()) {
        PaymentJson(
            id = it.id.value,
            receiver = it.receiver.asStorageBillsList(),
            cost = it.cost,
            description = it.description,
            creationDate = it.creationDate.asStorageDate()
        )
    }

    private fun Member.asStorageBillsList() = MemberJson(name = name)

    private fun Date.asStorageDate() = value.format(dateFormatter)

    private fun ComputationJson.asDomainModel() =
        Computation(
            id = ComputationId(id),
            title = title,
            bills = bills.asDomainBills(),
            members = members.map { it.asDomainModel() },
            creationDate = creationDate.asDomainDate(),
            isCompleted = isCompleted
        )

    private fun List<ComputationJson>.asDomainModel() =
        map { it.asDomainModel() }

    private fun List<BillJson>.asDomainBills() = map {
        Bill(
            id = BillId(it.id),
            title = it.title,
            backer = it.backer.asDomainModel(),
            payments = it.payments.asDomainPayments(),
            creationDate = it.creationDate.asDomainDate()
        )
    }

    private fun List<PaymentJson>.asDomainPayments() = map {
        Payment(
            id = PaymentId(it.id),
            receiver = it.receiver.asDomainModel(),
            cost = it.cost,
            description = it.description,
            creationDate = it.creationDate.asDomainDate()
        )
    }

    private fun MemberJson.asDomainModel() = Member(name = name)

    private fun String.asDomainDate() = Date(dateFormatter.parse(this, ZonedDateTime::from))

    private fun newUUID() = UUID.randomUUID().toString()

    companion object {
        private const val MEMORY_FILE_NAME = "memory.json"
    }

}
