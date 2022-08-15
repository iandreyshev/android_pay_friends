package ru.iandreyshev.stale.presentation.transactionEditor

sealed interface Message {
    data class InitComplete(val state: State) : Message
}
