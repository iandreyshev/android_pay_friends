package ru.iandreyshev.stale.presentation.paymentsList

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.payments.ArchivePaymentUseCase
import ru.iandreyshev.stale.domain.payments.GetPaymentsListUseCase
import ru.iandreyshev.stale.domain.payments.PaymentsStorage
import ru.iandreyshev.stale.presentation.utils.SingleStateViewModel

class PaymentsListViewModel(
    private val storage: PaymentsStorage,
    private val archivePayment: ArchivePaymentUseCase,
    getList: GetPaymentsListUseCase,
) : SingleStateViewModel<State, Event>(
    initialState = State.default()
) {

    init {
        getList()
            .onEach { modifyState { copy(payments = it) } }
            .launchIn(viewModelScope)
    }

    fun onOpenPayment(id: PaymentId) {
        event(Event.NavigateToPayment(id))
    }

    fun onAddTransaction(id: PaymentId) {
        event(Event.NavigateToTransactionEditor(id))
    }

    fun onNewPayment() {
        event(Event.NavigateToPaymentEditor())
    }

    fun onEditPayment(id: PaymentId) {
        event(Event.NavigateToPaymentEditor(id))
    }

    fun onArchivePayment(id: PaymentId, isArchive: Boolean) {
        viewModelScope.launch {
            archivePayment(id, isArchive)
        }
    }

    fun onDeletePayment(id: PaymentId) {
        viewModelScope.launch {
            storage.remove(id)
        }
    }

}
