package ru.iandreyshev.stale.data.payment

import io.bloco.faker.Faker
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.Payment
import ru.iandreyshev.stale.domain.core.PaymentId
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
        repeat(Random.nextInt(1, 50)) {
            add(
                Payment(
                    id = randomPaymentId(),
                    name = faker.book.title(),
                    members = members,
                    creationDate = faker.date.backward().toString(),
                    transactions = emptyList(),
                    isArchived = false
                )
            )
        }
    }
}

fun randomPaymentId() =
    PaymentId(Random.nextInt().toString())
