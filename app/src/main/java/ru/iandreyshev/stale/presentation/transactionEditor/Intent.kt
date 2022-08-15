package ru.iandreyshev.stale.presentation.transactionEditor

sealed interface Intent {
    data class OnProducerFieldChanged(val text: String) : Intent
}
