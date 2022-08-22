package ru.iandreyshev.payfriends.domain.billEditor

import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.core.*
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.time.DateProvider
import ru.iandreyshev.payfriends.system.Dispatchers

class SaveBillUseCase(
    private val computationId: ComputationId,
    private val storage: Storage,
    private val dateProvider: DateProvider,
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(draft: BillDraft): Result<Unit> =
        withContext(dispatchers.io) {
//            val payment = storage.get(computationId)
//                ?: return@withContext Result.Error(ErrorType.Unknown)
//
//            val transactionsWithDate = draft.payments
//                .map { it.copy(creationDate = dateProvider.currentDate()) }
//
//            val existedBills = payment.bills.associateBy { it.id }
//            val existedBill = existedBills[draft.id] ?: Bill(
//                id = BillId.none(),
//                backer = draft.backer,
//
//            )
//            val billWithDate = existedBill.copy(
//                payments = transactionsWithDate,
//                creationDate = dateProvider.currentDate()
//            )
//
//            val existedTransactions = existedBill?.payments
//                .orEmpty()
//                .associateBy { it.id }
//                .toMutableMap()
//            val resultTransactions = mutableListOf<Payment>()
//
//            transactionsWithDate.forEach { new ->
//                existedTransactions.remove(new.id)
//                resultTransactions.add(new)
//            }
//            resultTransactions.addAll(existedTransactions.values)
//
//            storage.save(payment.copy(bills = listOf()))

            Result.Success(Unit)
        }

}
