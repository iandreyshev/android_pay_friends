package ru.iandreyshev.payfriends.domain.core

@JvmInline
value class TransactionId(val value: String) {

    companion object {
        fun none() = TransactionId("")
    }

}
