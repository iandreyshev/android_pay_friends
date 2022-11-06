package ru.iandreyshev.payfriends.domain.calc

import ru.iandreyshev.payfriends.domain.core.Bill
import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.Participants
import ru.iandreyshev.payfriends.domain.core.Transfer
import ru.iandreyshev.payfriends.system.Dispatchers
import javax.inject.Inject
import kotlin.math.absoluteValue

class CalcResult2UseCase
@Inject constructor(
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke(bills: List<Bill>): List<Transfer> {
        val result = mutableListOf<Transfer>()
        val table = mutableMapOf<Member, Int>()

        bills.forEach { bill ->
            bill.payments.forEach { payment ->
                val cost = payment.cost

                if (table.containsKey(payment.receiver)) {
                    table[payment.receiver] = table[payment.receiver]!! - cost
                } else {
                    table[payment.receiver] = -cost
                }

                if (table.containsKey(bill.backer)) {
                    table[bill.backer] = table[bill.backer]!! - cost
                } else {
                    table[bill.backer] = cost
                }
            }
        }

        while (table.isNotEmpty()) {
            result += removeEquals(table)

            val backers = mutableListOf<Pair<Int, Member>>()
            val receivers = mutableListOf<Pair<Int, Member>>()

            table.forEach { (member, i) ->
                when (i > 0) {
                    true -> backers.add(i to member)
                    else -> receivers.add(i to member)
                }
            }

            backers.sortBy { it.first.absoluteValue }
            receivers.sortBy { it.first.absoluteValue }

            break
        }

        return result
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

}
