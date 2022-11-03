package ru.iandreyshev.payfriends.presentation.computation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iandreyshev.payfriends.domain.calc.CalcResultUseCase
import ru.iandreyshev.payfriends.domain.computation.GetComputationHistoryUseCase
import ru.iandreyshev.payfriends.domain.computation.GetComputationUseCase
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.presentation.utils.SingleStateViewModel
import javax.inject.Inject

class ComputationViewModel
@Inject constructor(
    private val calcResult: CalcResultUseCase,
    private val getComputation: GetComputationUseCase,
    private val getComputationHistory: GetComputationHistoryUseCase
) : SingleStateViewModel<State, Event>(State.default()) {

    fun onViewCreated(id: ComputationId, name: String) {
        viewModelScope.launch {
            modifyState { copy(id = id, name = name) }

            val computation = getComputation(getState().id) ?: run {
                event { Event.Exit(ErrorType.Unknown) }
                return@launch
            }
            val history = getComputationHistory(computation)
            val result = calcResult(computation.bills)

            modifyState {
                copy(
                    name = computation.title,
                    creationDate = computation.creationDate,
                    history = history,
                    result = result,
                    isLoading = false
                )
            }
        }
    }

    fun onAddBill() {
        event { Event.OpenBillEditor(getState().id) }
    }

    fun onExit() {
        event { Event.Exit() }
    }

}
