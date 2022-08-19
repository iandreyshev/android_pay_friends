package ru.iandreyshev.stale.domain.core

@JvmInline
value class TransactionId(val value: String) {

    companion object {
        fun none() = TransactionId("")
    }

}
