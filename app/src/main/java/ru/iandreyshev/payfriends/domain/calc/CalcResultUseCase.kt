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
                    Transfer(
                        participants = Participants(bill.backer, payment.receiver),
                        cost = payment.cost
                    )
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

}
