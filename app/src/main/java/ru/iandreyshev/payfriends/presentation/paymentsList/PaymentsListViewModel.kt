package ru.iandreyshev.payfriends.presentation.paymentsList

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.payments.GetPaymentsListUseCase
import ru.iandreyshev.payfriends.domain.payments.MarkPaymentAsCompletedUseCase
import ru.iandreyshev.payfriends.domain.payments.PaymentSummary
import ru.iandreyshev.payfriends.domain.payments.PaymentsStorage
import ru.iandreyshev.payfriends.presentation.utils.SingleStateViewModel

class PaymentsListViewModel(
    isListOfActivePayments: Boolean,
    private val storage: PaymentsStorage,
    private val completePayment: MarkPaymentAsCompletedUseCase,
    getList: GetPaymentsListUseCase,
) : SingleStateViewModel<State, Event>(
    initialState = State.default().copy(
        isListOfActivePayments = isListOfActivePayments
    )
) {

    init {
        getList(filter = GetPaymentsListUseCase.Filter(isListOfActivePayments))
            .onEach { modifyState { copy(payments = it) } }
            .launchIn(viewModelScope)
    }

    fun onOpenPayment(payment: PaymentSummary) {
        event(Event.NavigateToPayment(payment.id, payment.name))
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

    fun onCompletePayment(id: PaymentId, isCompleted: Boolean) {
        viewModelScope.launch {
            completePayment(id, isCompleted)
        }
    }

    fun onDeletePayment(id: PaymentId) {
        viewModelScope.launch {
            storage.remove(id)
        }
    }

}
