package ru.iandreyshev.stale.domain.calc

import ru.iandreyshev.stale.domain.core.Transaction
import ru.iandreyshev.stale.domain.core.TransactionParticipants
import java.lang.IllegalStateException
import kotlin.math.abs

class CalcResultUseCase {

    operator fun invoke(transactions: List<Transaction>): List<Transaction> {
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
                        transaction1 = Transaction(entry.key, entry.value),
                        transaction2 = Transaction(mirror, mirrorCost)
                    )

                    optimized.remove(mirror)
                    val resultCost = abs(entry.value - mirrorCost)
                    if (resultCost > 0) {
                        optimized[resultParticipants] = resultCost
                    }
                }
            }
        }

        return optimized.map { Transaction(it.key, it.value) }
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