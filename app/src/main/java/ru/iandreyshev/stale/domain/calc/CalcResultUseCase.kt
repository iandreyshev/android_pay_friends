package ru.iandreyshev.stale.domain.calc

import kotlinx.coroutines.withContext
import ru.iandreyshev.stale.domain.core.Transaction
import ru.iandreyshev.stale.domain.core.TransactionId
import ru.iandreyshev.stale.domain.core.TransactionParticipants
import ru.iandreyshev.stale.domain.time.Date
import ru.iandreyshev.stale.system.Dispatchers
import kotlin.math.abs

class CalcResultUseCase(
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(transactions: List<Transaction>): List<Transaction> =
        withContext(dispatchers.io) {
            val totalCostOfParticipants: Map<TransactionParticipants, Int> =
                transactions.groupBy { it.participants }
                    .mapValues { ts -> ts.value.sumOf { it.cost } }

            val optimized = mutableMapOf<TransactionParticipants, Int>()

            totalCostOfParticipants.forEach { entry ->
                val mirror = entry.key.mirror()
                when (val mirrorCost = optimized[mirror]) {
                    null -> optimized[entry.key] = entry.value
                    else -> {
                        val resultParticipants = getResultParticipants(
                            transaction1 = Transaction(TransactionId.none(), entry.key, entry.value, "", Date("")),
                            transaction2 = Transaction(TransactionId.none(), mirror, mirrorCost, "", Date(""))
                        )

                        optimized.remove(mirror)
                        val resultCost = abs(entry.value - mirrorCost)
                        if (resultCost > 0) {
                            optimized[resultParticipants] = resultCost
                        }
                    }
                }
            }

            optimized.map {
                Transaction(TransactionId.none(), it.key, it.value, "", Date(""))
            }
        }

    private fun getResultParticipants(
        transaction1: Transaction,
        transaction2: Transaction
    ): TransactionParticipants {
        if (!transaction1.isMirrorOf(transaction2)) {
            throw IllegalStateException("Transaction $transaction1 is not mirror of $transaction2")
        }

        if (transaction1.cost > transaction2.cost) {
            return transaction1.participants
        }

        return transaction2.participants
    }

}
