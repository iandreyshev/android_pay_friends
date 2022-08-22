package ru.iandreyshev.payfriends.domain.core

@JvmInline
value class BillId(val value: String) {

    companion object {
        fun none() = BillId("")
    }

}
