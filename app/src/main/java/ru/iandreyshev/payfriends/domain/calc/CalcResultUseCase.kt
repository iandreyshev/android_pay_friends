package ru.iandreyshev.payfriends.domain.calc

import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.core.Bill
import ru.iandreyshev.payfriends.domain.core.Participants
import ru.iandreyshev.payfriends.domain.core.Transfer
import ru.iandreyshev.payfriends.system.Dispatchers
import javax.inject.Inject
import kotlin.math.abs

class CalcResultUseCase
@Inject constructor(
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(bills: List<Bill>): List<Transfer> =
        withContext(dispatchers.io) {
            val transfers = bills.flatMap { bill ->
                bill.payments.map { payment ->
                    Transfer(Participants(bill.backer, payment.receiver), payment.cost)
                }
            }
            val totalCostOfParticipants: Map<Participants, Int> =
                transfers.groupBy { it.participants }
                    .mapValues { ts -> ts.value.sumOf { it.cost } }

            val optimized = mutableMapOf<Participants, Int>()

            totalCostOfParticipants.forEach { entry ->
                val mirror = entry.key.mirror()
                when (val mirrorCost = optimized[mirror]) {
                    null -> optimized[entry.key] = entry.value
                    else -> {
                        val resultParticipants = getResultParticipants(
                            transfer1 = Transfer(entry.key, entry.value),
                            transfer2 = Transfer(mirror, mirrorCost)
                        )

                        optimized.remove(mirror)
                        val resultCost = abs(entry.value - mirrorCost)
                        if (resultCost > 0) {
                            optimized[resultParticipants] = resultCost
                        }
                    }
                }
            }

            optimized.map { Transfer(it.key, it.value) }
        }

    private fun getResultParticipants(
        transfer1: Transfer,
        transfer2: Transfer
    ): Participants {
        if (!transfer1.isMirrorOf(transfer2)) {
            throw IllegalStateException("Transfer $transfer1 is not mirror of $transfer2")
        }

        if (transfer1.cost > transfer2.cost) {
            return transfer1.participants
        }

        return transfer2.participants
    }

}
