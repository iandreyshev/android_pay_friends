package ru.iandreyshev.payfriends.data.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SettingsJson(
    @SerialName("is_smart_algorithm")
    val isSmartAlgorithm: Boolean
)
