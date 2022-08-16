package ru.iandreyshev.stale.presentation.transactionEditor

import ru.iandreyshev.stale.domain.core.Member

sealed interface Intent {
    // Common
    object OnSave : Intent
    object OnExit : Intent

    // Producer field
    data class OnProducerFieldChanged(val text: String) : Intent
    data class OnProducerSelected(val member: Member): Intent
    data class OnProducerCandidateSelected(val name: String): Intent
    object OnRemoveProducer : Intent
}
