package ru.iandreyshev.payfriends.presentation.transactionEditor

sealed interface Label {
    object ExitWithWarning : Label
    class Exit(val message: String? = null) : Label
    sealed interface Error : Label {
        object InvalidProducerCandidate : Error
        object Unknown : Error
    }
}
