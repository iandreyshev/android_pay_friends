package ru.iandreyshev.payfriends

import kotlinx.coroutines.runBlocking
import ru.iandreyshev.payfriends.domain.calc.CalcResultUseCase
import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.Transaction
import ru.iandreyshev.payfriends.domain.core.TransactionId
import ru.iandreyshev.payfriends.domain.core.TransactionParticipants
import ru.iandreyshev.payfriends.domain.time.Date
import ru.iandreyshev.payfriends.system.AppDispatchers

fun main() {
    var line = readLine()
    val transactions = mutableListOf<Transaction>()

    while (line != null && line != "вых") {
        val splitted = line.split(" ")
        transactions.add(
            Transaction(
                id = TransactionId.none(),
                participants = TransactionParticipants(
                    producer = Member(splitted[0]),
                    receiver = Member(splitted[1])
                ),
                cost = splitted[2].toInt(),
                description = "",
                creationDate = Date("")
            )
        )
        line = readLine()
    }

    runBlocking {
        CalcResultUseCase(AppDispatchers)(transactions).forEach {
            println(it.toString())
        }
    }
}
