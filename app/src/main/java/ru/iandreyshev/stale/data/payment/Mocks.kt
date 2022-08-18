package ru.iandreyshev.stale.data.payment

import io.bloco.faker.Faker
import ru.iandreyshev.stale.domain.core.*
import kotlin.random.Random

fun createMock1(): List<Payment> {
    val faker = Faker()
    val members = mutableListOf<Member>()

    repeat(Random.nextInt(1, 20)) {
        members.add(
            Member(name = faker.name.firstName())
        )
    }

    return mutableListOf<Payment>().apply {
        return@apply
        repeat(Random.nextInt(0, 20)) {
            add(
                Payment(
                    id = randomPaymentId(),
                    name = faker.book.title(),
                    members = members,
                    creationDate = faker.date.backward().toString(),
                    transactions = mutableListOf<Transaction>().apply {
                        repeat(Random.nextInt(10, 100)) {
                            add(
                                Transaction(
                                    id = randomTransactionId(),
                                    participants = TransactionParticipants(
                                        producer = Member(faker.name.name()),
                                        receiver = Member(faker.name.name())
                                    ),
                                    cost = 0,
                                    description = ""
                                )
                            )
                        }
                    },
                    isCompleted = false
                )
            )
        }
    }
}

fun randomPaymentId() =
    PaymentId(Random.nextInt().toString())

fun randomTransactionId() =
    TransactionId(Random.nextInt().toString())
