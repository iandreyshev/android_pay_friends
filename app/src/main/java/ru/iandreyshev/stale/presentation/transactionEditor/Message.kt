package ru.iandreyshev.stale.presentation.transactionEditor

sealed interface Message {
    data class Started(val state: State) : Message
}
