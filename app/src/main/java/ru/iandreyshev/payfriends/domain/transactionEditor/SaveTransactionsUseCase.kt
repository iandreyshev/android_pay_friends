package ru.iandreyshev.payfriends.domain.transactionEditor

import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.core.Result
import ru.iandreyshev.payfriends.domain.core.Transaction
import ru.iandreyshev.payfriends.domain.payments.PaymentsStorage
import ru.iandreyshev.payfriends.domain.time.DateProvider
import ru.iandreyshev.payfriends.system.Dispatchers

class SaveTransactionsUseCase(
    private val paymentId: PaymentId,
    private val storage: PaymentsStorage,
    private val dateProvider: DateProvider,
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(transactions: List<Transaction>): Result<Unit> =
        withContext(dispatchers.io) {
            val transactionsWithDate = transactions
                .map { it.copy(creationDate = dateProvider.currentDate()) }

            val payment = storage.get(paymentId)
                ?: return@withContext Result.Error(ErrorType.Unknown)

            val existedTransactions = payment.transactions
                .associateBy { it.id }
                .toMutableMap()
            val resultTransactions = mutableListOf<Transaction>()

            transactionsWithDate.forEach { new ->
                existedTransactions.remove(new.id)
                resultTransactions.add(new)
            }
            resultTransactions.addAll(existedTransactions.values)

            storage.save(payment.copy(transactions = transactionsWithDate))

            Result.Success(Unit)
        }

}
