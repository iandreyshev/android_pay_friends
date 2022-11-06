package ru.iandreyshev.payfriends.presentation.computation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.iandreyshev.payfriends.domain.calc.CalcResultUseCase
import ru.iandreyshev.payfriends.domain.computation.DeleteBillUseCase
import ru.iandreyshev.payfriends.domain.computation.GetComputationHistoryUseCase
import ru.iandreyshev.payfriends.domain.computation.GetComputationUseCase
import ru.iandreyshev.payfriends.domain.computationsList.GetComputationsListUseCase
import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.presentation.utils.SingleStateViewModel
import javax.inject.Inject

class ComputationViewModel
@Inject constructor(
    private val calcResult: CalcResultUseCase,
    private val getComputation: GetComputationUseCase,
    private val getComputationHistory: GetComputationHistoryUseCase,
    private val deleteBill: DeleteBillUseCase,
    private val getComputationsList: GetComputationsListUseCase
) : SingleStateViewModel<State, Event>(State.default()) {

    fun onViewCreated(id: ComputationId, name: String) {
        modifyState { copy(id = id, name = name) }
        loadData()

        getComputationsList.invoke(GetComputationsListUseCase.Filter(false))
            .onEach { loadData() }
            .launchIn(viewModelScope)
    }

    fun onAddBill() {
        event { Event.OpenBillEditor(getState().id) }
    }

    fun onExit() {
        event { Event.Exit() }
    }

    fun onEditBill(id: BillId) {
    }

    fun onDeleteBill(id: BillId) {
        viewModelScope.launch {
            deleteBill(id)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
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

}
