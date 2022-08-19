package ru.iandreyshev.stale.presentation.transactionEditor

import ru.iandreyshev.stale.domain.core.Member

sealed interface Intent {
    // Common
    object OnSave : Intent
    object OnBack : Intent

    // Producer field
    data class OnProducerFieldChanged(val query: String) : Intent
    data class OnProducerSelected(val producer: Member) : Intent
    data class OnProducerCandidateSelected(val query: String) : Intent
    object OnRemoveProducer : Intent

    // Receiver field
    data class OnReceiverFieldChanged(val query: String) : Intent
    data class OnReceiverSelected(val receiver: Member) : Intent
    data class OnReceiverCandidateSelected(val query: String) : Intent

    // Transactions
    data class OnRemoveTransaction(val position: Int) : Intent
    data class OnCostChanged(val position: Int, val cost: String) : Intent
    data class OnDescriptionChanged(val position: Int, val description: String): Intent
}
