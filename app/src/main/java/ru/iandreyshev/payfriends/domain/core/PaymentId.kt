package ru.iandreyshev.payfriends.domain.core

@JvmInline
value class PaymentId(val value: String) {

    companion object {
        fun none() = PaymentId("")
    }

}
