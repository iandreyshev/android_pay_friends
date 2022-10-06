package ru.iandreyshev.payfriends.data.storage.memory

import io.bloco.faker.Faker
import ru.iandreyshev.payfriends.domain.core.*
import ru.iandreyshev.payfriends.domain.time.Date
import kotlin.random.Random

fun createMock1(): List<Computation> {
    val faker = Faker()
    val members = mutableListOf<Member>()

    repeat(Random.nextInt(1, 20)) {
        members.add(
            Member(name = faker.name.firstName())
        )
    }

    return mutableListOf<Computation>().apply {
        return@apply
        repeat(Random.nextInt(0, 20)) {
            add(
                Computation(
                    id = randomComputationId(),
                    title = faker.book.title(),
                    members = members,
                    creationDate = Date(faker.date.backward().toString()),
                    bills = listOf(
                        Bill(
                            id = BillId.none(),
                            title = faker.book.title(),
                            backer = Member(faker.name.name()),
                            payments = mutableListOf<Payment>().apply {
                                repeat(Random.nextInt(10, 100)) {
                                    add(
                                        Payment(
                                            id = randomPaymentId(),
                                            receiver = Member(faker.name.name()),
                                            cost = 0,
                                            description = "",
                                            creationDate = Date("")
                                        )
                                    )
                                }
                            },
                            creationDate = Date("")
                        )
                    ),
                    isCompleted = false
                )
            )
        }
    }
}

fun randomComputationId() =
    ComputationId(Random.nextInt().toString())

fun randomBillId() =
    BillId(Random.nextInt().toString())

fun randomPaymentId() =
    PaymentId(Random.nextInt().toString())
