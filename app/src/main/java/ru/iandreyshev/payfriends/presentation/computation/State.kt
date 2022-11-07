package ru.iandreyshev.payfriends.presentation.computation

import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.payfriends.domain.calc.CalcResultWithSmartAlgorithmUseCase
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.core.HistoryBill
import ru.iandreyshev.payfriends.domain.core.Transfer
import ru.iandreyshev.payfriends.domain.time.Date

data class State(
    val id: ComputationId,
    val name: String,
    val creationDate: Date,
    val result: List<Transfer>,
    val history: List<HistoryBill>,
    val isLoading: Boolean,
    val isSmartAlgorithm: Boolean
) {

    companion object {
        fun default() = State(
            id = ComputationId(""),
            name = "",
            creationDate = Date(ZonedDateTime.now()),
            result = emptyList(),
            history = emptyList(),
            isLoading = true,
            isSmartAlgorithm = false
        )
    }

}
