package ru.iandreyshev.payfriends

import kotlinx.coroutines.runBlocking
import ru.iandreyshev.payfriends.data.storage.memory.randomBillId
import ru.iandreyshev.payfriends.domain.calc.CalcResultUseCase
import ru.iandreyshev.payfriends.domain.core.Bill
import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.Payment
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.time.Date
import ru.iandreyshev.payfriends.system.AppDispatchers

fun main() {
    var line = readLine()
    val bills = mutableListOf<Bill>()

    while (line != null && line != "вых") {
        val splitted = line.split(" ")
        bills.add(
            Bill(
                id = randomBillId(),
                title = "",
                backer = Member(splitted[0]),
                creationDate = Date(""),
                payments = listOf(
                    Payment(
                        id = PaymentId.none(),
                        receiver = Member(splitted[1]),
                        cost = splitted[2].toInt(),
                        description = "",
                        creationDate = Date("")
                    )
                )
            )
        )
        line = readLine()
    }

    runBlocking {
        CalcResultUseCase(AppDispatchers)(bills).forEach {
            println(it.toString())
        }
    }
}
