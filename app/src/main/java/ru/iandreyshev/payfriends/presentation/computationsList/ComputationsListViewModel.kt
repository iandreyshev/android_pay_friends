package ru.iandreyshev.payfriends.presentation.computationsList

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.computationsList.GetComputationsListUseCase
import ru.iandreyshev.payfriends.domain.computationsList.MarkComputationAsCompletedUseCase
import ru.iandreyshev.payfriends.domain.computationsList.ComputationSummary
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.presentation.utils.SingleStateViewModel

class ComputationsListViewModel(
    isListOfActiveComputations: Boolean,
    private val storage: Storage,
    private val completeComputation: MarkComputationAsCompletedUseCase,
    getList: GetComputationsListUseCase,
) : SingleStateViewModel<State, Event>(
    initialState = State.default().copy(
        isListOfActivePayments = isListOfActiveComputations
    )
) {

    init {
        getList(filter = GetComputationsListUseCase.Filter(isListOfActiveComputations))
            .onEach { modifyState { copy(computations = it) } }
            .launchIn(viewModelScope)
    }

    fun onOpenPayment(computation: ComputationSummary) {
        event(Event.NavigateToPayment(computation.id, computation.title))
    }

    fun onAddBill(id: ComputationId) {
        event(Event.NavigateToBillEditor(id))
    }

    fun onNewComputation() {
        event(Event.NavigateToComputationEditor())
    }

    fun onEditComputation(id: ComputationId) {
        event(Event.NavigateToComputationEditor(id))
    }

    fun onCompletePayment(id: ComputationId, isCompleted: Boolean) {
        viewModelScope.launch {
            completeComputation(id, isCompleted)
        }
    }

    fun onDeletePayment(id: ComputationId) {
        viewModelScope.launch {
            storage.remove(id)
        }
    }

}
