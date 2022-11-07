package ru.iandreyshev.payfriends.domain.calc

import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.core.Bill
import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.Participants
import ru.iandreyshev.payfriends.domain.core.Transfer
import ru.iandreyshev.payfriends.system.Dispatchers
import javax.inject.Inject
import kotlin.math.absoluteValue

class CalcResultWithSmartAlgorithmUseCase
@Inject constructor(
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(bills: List<Bill>): List<Transfer> =
        withContext(dispatchers.io) {
            val result = mutableListOf<Transfer>()
            val table = fillTable(bills)

            while (table.isNotEmpty()) {
                result += removeEquals(table)
                result += payByDebts(table)
            }

            result
        }

    private fun fillTable(bills: List<Bill>): MutableMap<Member, Int> {
        val table = mutableMapOf<Member, Int>()
        bills.forEach { bill ->
            bill.payments.forEach { payment ->
                val cost = payment.cost

                when {
                    table.containsKey(payment.receiver) ->
                        when (val debt = table[payment.receiver]!! - cost) {
                            0 -> table.remove(payment.receiver)
                            else -> table[payment.receiver] = debt
                        }
                    else -> table[payment.receiver] = -cost
                }

                when {
                    table.containsKey(bill.backer) ->
                        when (val debt = table[bill.backer]!! + cost) {
                            0 -> table.remove(bill.backer)
                            else -> table[bill.backer] = debt
                        }
                    else -> table[bill.backer] = cost
                }
            }
        }

        return table
    }

    private fun removeEquals(table: MutableMap<Member, Int>): List<Transfer> {
        val result = mutableListOf<Transfer>()
        val copy = table.toMap()

        while (true) {
            var hasChanges = false

            copy.any { copyEntity ->
                table.any { tableEntity ->
                    if (copyEntity.value == -tableEntity.value) {
                        hasChanges = true
                        table.remove(copyEntity.key)
                        table.remove(tableEntity.key)
                        result.add(
                            Transfer(
                                participants = when (copyEntity.value > tableEntity.value) {
                                    true -> Participants(copyEntity.key, tableEntity.key)
                                    else -> Participants(tableEntity.key, copyEntity.key)
                                },
                                cost = copyEntity.value.absoluteValue
                            )
                        )
                        true
                    } else {
                        false
                    }
                }
            }

            if (!hasChanges) {
                return result
            }
        }
    }

    private fun payByDebts(table: MutableMap<Member, Int>): List<Transfer> {
        val result = mutableListOf<Transfer>()
        val positive = mutableListOf<Pair<Int, Member>>()
        val negative = mutableListOf<Pair<Int, Member>>()

        table.forEach { (member, i) ->
            when (i > 0) {
                true -> positive.add(i to member)
                else -> negative.add(i to member)
            }
        }

        positive.sortByDescending { it.first.absoluteValue }
        negative.sortByDescending { it.first.absoluteValue }

        negative.forEach { debtEntity ->
            var potential = debtEntity.first.absoluteValue

            do {
                val receiverEntity = positive.first()
                val debt = receiverEntity.first
                val participants = Participants(receiverEntity.second, debtEntity.second)

                if (potential > debt) {
                    potential -= debt
                    table[debtEntity.second] = debtEntity.first + debt
                    table.remove(receiverEntity.second)
                    positive.removeFirst()
                    result += Transfer(participants, debt)
                } else {
                    table[receiverEntity.second] = debt - potential
                    table.remove(debtEntity.second)
                    if (potential == debt) {
                        table.remove(receiverEntity.second)
                    }
                    result += Transfer(participants, potential)
                    return result
                }
            } while (potential != 0)
        }

        return result
    }

}
