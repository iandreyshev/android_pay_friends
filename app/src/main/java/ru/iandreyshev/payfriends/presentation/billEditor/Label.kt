package ru.iandreyshev.payfriends.presentation.billEditor

sealed interface Label {
    object ExitWithWarning : Label
    object ScrollToBottom : Label
    class Exit(val message: String? = null) : Label
    sealed interface Error : Label {
        object InvalidProducerCandidate : Error
        object InvalidCost : Error
        object Unknown : Error
    }
}
