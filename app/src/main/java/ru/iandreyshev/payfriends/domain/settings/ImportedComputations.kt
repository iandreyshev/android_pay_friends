package ru.iandreyshev.payfriends.domain.settings

sealed interface ImportedComputations

data class JsonComputations(val text: String) : ImportedComputations
