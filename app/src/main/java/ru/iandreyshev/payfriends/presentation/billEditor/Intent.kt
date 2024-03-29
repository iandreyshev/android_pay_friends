package ru.iandreyshev.payfriends.presentation.billEditor

import ru.iandreyshev.payfriends.domain.core.Member

sealed interface Intent {
    // Common
    object OnSave : Intent
    object OnBack : Intent

    // Producer field
    data class OnProducerFieldChanged(val query: String) : Intent
    data class OnProducerSelected(val producer: Member) : Intent
    data class OnProducerCandidateSelected(val query: String) : Intent
    data class OnCommonBillToggle(val isTurnOn: Boolean) : Intent
    data class OnCommonBillForUserToggle(val isTurnOn: Boolean) : Intent
    data class OnCommonBillCostChanged(val cost: Int) : Intent
    data class OnCommonBillRemoveMember(val member: Member) : Intent
    object OnRemoveProducer : Intent

    // Title
    data class OnTitleChanged(val text: String) : Intent

    // Receiver field
    data class OnReceiverFieldChanged(val query: String) : Intent
    data class OnReceiverSelected(val receiver: Member) : Intent
    data class OnReceiverCandidateSelected(val query: String) : Intent

    // Payments
    data class OnRemovePayment(val position: Int) : Intent
    data class OnCostChanged(val position: Int, val cost: String) : Intent
    data class OnDescriptionChanged(val position: Int, val description: String) : Intent
}
