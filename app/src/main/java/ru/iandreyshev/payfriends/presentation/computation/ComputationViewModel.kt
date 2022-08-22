package ru.iandreyshev.payfriends.presentation.computation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iandreyshev.payfriends.domain.calc.CalcResultUseCase
import ru.iandreyshev.payfriends.domain.computation.GetComputationUseCase
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.presentation.utils.SingleStateViewModel

class ComputationViewModel(
    id: ComputationId,
    name: String,
    private val calcResult: CalcResultUseCase,
    private val getComputation: GetComputationUseCase
) : SingleStateViewModel<State, Event>(
    initialState = State.default().copy(
        id = id,
        name = name
    )
) {

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val computation = getComputation(getState().id) ?: run {
                event { Event.Exit(ErrorType.Unknown) }
                return@launch
            }
            val transfers = computation.getAllTransfers()
            val result = calcResult(computation.bills)

            modifyState {
                copy(
                    name = computation.title,
                    creationDate = computation.creationDate,
                    history = transfers,
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
