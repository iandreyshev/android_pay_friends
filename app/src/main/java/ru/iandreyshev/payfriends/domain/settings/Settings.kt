package ru.iandreyshev.payfriends.domain.settings

data class Settings(val isSmartAlgorithm: Boolean) {

    companion object {
        fun default() = Settings(false)
    }

}
