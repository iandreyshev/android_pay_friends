package ru.iandreyshev.stale.presentation.paymentsList

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.iandreyshev.stale.domain.payment.PaymentId
import ru.iandreyshev.stale.domain.payment.PaymentsStorage
import ru.iandreyshev.stale.presentation.utils.SingleStateViewModel

class PaymentsListViewModel(
    storage: PaymentsStorage
) : SingleStateViewModel<State, Event>(
    initialState = State.default()
) {

    init {
        storage.observe()
            .onEach { modifyState { copy(payments = it) } }
            .launchIn(viewModelScope)
    }

    fun onOpenPayment(id: PaymentId) {
    }

    fun onAddTransaction(id: PaymentId) {
    }

    fun onNewPayment() {
        event(Event.NavigateToPaymentEditor())
    }

    fun onEditPayment(id: PaymentId) {
        event(Event.NavigateToPaymentEditor(id))
    }

    fun onArchivePayment(id: PaymentId) {
    }

    fun onDeletePayment(id: PaymentId) {
    }

}
