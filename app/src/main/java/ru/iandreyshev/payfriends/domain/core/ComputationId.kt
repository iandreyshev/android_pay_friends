package ru.iandreyshev.payfriends.domain.core

@JvmInline
value class ComputationId(val value: String) {

    companion object {
        fun none() = ComputationId("")
    }

}
