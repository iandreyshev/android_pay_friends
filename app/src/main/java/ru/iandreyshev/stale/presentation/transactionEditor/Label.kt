package ru.iandreyshev.stale.presentation.transactionEditor

sealed interface Label {
    class Exit(val error: String?) : Label
    sealed interface Error : Label {
        object InvalidProducerCandidate : Error
    }
}
