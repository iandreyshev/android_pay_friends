package ru.iandreyshev.stale.presentation.transactionEditor

sealed interface Action {
    object InvokeOnStart : Action
}
