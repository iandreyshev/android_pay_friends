package ru.iandreyshev.payfriends.data

import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.core.PaymentId
import kotlin.random.Random

fun randomComputationId() =
    ComputationId(Random.nextInt().toString())

fun randomBillId() =
    BillId(Random.nextInt().toString())

fun randomPaymentId() =
    PaymentId(Random.nextInt().toString())
