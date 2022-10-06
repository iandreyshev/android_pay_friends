package ru.iandreyshev.payfriends.data.storage.json

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.*
import ru.iandreyshev.payfriends.domain.time.Date
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class JsonStorage
//@Inject constructor() : Storage {
//
//    private val memory: String = ""
//
//    override suspend fun save(computation: Computation): Computation {
//        val computations = getComputations()
//    }
//
//    override suspend fun get(id: ComputationId): Computation? {
//        return getComputations()
//            .asDomainModel()
//            .firstOrNull { it.id == id }
//    }
//
//    override suspend fun remove(id: ComputationId) {
////        getComputations()
////            .removeIf {  }
//    }
//
//    override fun observable(): Flow<List<Computation>> {
//        TODO("observable not implemented")
//    }
//
//    private fun getComputations(): MutableList<ComputationJson> =
//        Json.decodeFromString(string = memory)
//
//    private fun Computation.asStorageBillsList() =
//        ComputationJson(
//            id = id.value,
//            title = title,
//            bills = bills.asStorageBillsList(),
//            members = members.map { it.asStorageBillsList() },
//            creationDate = creationDate.asStorageBillsList(),
//            isCompleted = isCompleted
//        )
//
//    private fun List<Bill>.asStorageBillsList() = map {
//        BillJson(
//            id = it.id.value,
//            title = it.title,
//            backer = it.backer.asStorageBillsList(),
//            payments = it.payments.asStoragePaymentsList(),
//            creationDate = it.creationDate.asStorageBillsList()
//        )
//    }
//
//    private fun List<Payment>.asStoragePaymentsList() = map {
//        PaymentJson(
//            id = it.id.value,
//            receiver = it.receiver.asStorageBillsList(),
//            cost = it.cost,
//            description = it.description,
//            creationDate = it.creationDate.asStorageBillsList()
//        )
//    }
//
//    private fun Member.asStorageBillsList() = MemberJson(name = name)
//
//    private fun Date.asStorageBillsList() = value
//
//    private fun List<ComputationJson>.asDomainModel() =
//        map {
//            Computation(
//                id = ComputationId(it.id),
//                title = it.title,
//                bills = it.bills.asDomainBills(),
//                members = it.members.map { it.asDomainModel() },
//                creationDate = it.creationDate.asDomainDate(),
//                isCompleted = it.isCompleted
//            )
//        }
//
//    private fun List<BillJson>.asDomainBills() = map {
//        Bill(
//            id = BillId(it.id),
//            title = it.title,
//            backer = it.backer.asDomainModel(),
//            payments = it.payments.asDomainPayments(),
//            creationDate = it.creationDate.asDomainDate()
//        )
//    }
//
//    private fun List<PaymentJson>.asDomainPayments() = map {
//        Payment(
//            id = PaymentId(it.id),
//            receiver = it.receiver.asDomainModel(),
//            cost = it.cost,
//            description = it.description,
//            creationDate = it.creationDate.asDomainDate()
//        )
//    }
//
//    private fun MemberJson.asDomainModel() = Member(name = name)
//
//    private fun String.asDomainDate() = Date(this)
//
//}
