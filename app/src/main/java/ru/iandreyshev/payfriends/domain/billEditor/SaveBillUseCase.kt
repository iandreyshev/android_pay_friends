package ru.iandreyshev.payfriends.domain.billEditor

import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.Bill
import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.Result
import ru.iandreyshev.payfriends.domain.time.DateProvider
import ru.iandreyshev.payfriends.system.Dispatchers
import javax.inject.Inject

class SaveBillUseCase
@Inject constructor(
    private val storage: Storage,
    private val dateProvider: DateProvider,
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(draft: BillDraft): Result<Unit> =
        withContext(dispatchers.io) {
            if (draft.payments.any { it.cost <= 0 }) {
                return@withContext Result.Error(ErrorType.InvalidCost)
            }

            val computation = storage.get(draft.computationId)
                ?: return@withContext Result.Error(ErrorType.Unknown)
            val existedBills = computation.bills.associateBy { it.id }
            val existedBill = existedBills[draft.id]
            val newBill = Bill(
                id = existedBill?.id ?: BillId.none(),
                title = draft.title,
                backer = draft.backer ?: return@withContext Result.Error(ErrorType.Unknown),
                payments = draft.payments,
                creationDate = existedBill?.creationDate ?: dateProvider.currentDate()
            )
            val newComputation = computation.copy(
                bills = existedBills.toMutableMap()
                    .apply { put(newBill.id, newBill) }
                    .values
                    .toList()
            )

            storage.save(newComputation)

            Result.Success(Unit)
        }

}
