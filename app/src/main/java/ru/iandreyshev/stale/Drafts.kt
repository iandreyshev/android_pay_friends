package ru.iandreyshev.stale

import ru.iandreyshev.stale.domain.calc.CalcResultUseCase
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.Transaction
import ru.iandreyshev.stale.domain.core.TransactionId
import ru.iandreyshev.stale.domain.core.TransactionParticipants

fun main() {
    var line = readLine()
    val transactions = mutableListOf<Transaction>()

    while (line != null && line != "вых") {
        val splitted = line.split(" ")
        transactions.add(
            Transaction(
                id = TransactionId(""),
                participants = TransactionParticipants(
                    producer = Member(splitted[0]),
                    receiver = Member(splitted[1])
                ),
                cost = splitted[2].toInt()
            )
        )
        line = readLine()
    }

    CalcResultUseCase()(transactions).forEach {
        println(it.toString())
    }
}
