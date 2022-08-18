package ru.iandreyshev.stale.presentation.payment

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iandreyshev.stale.domain.calc.CalcResultUseCase
import ru.iandreyshev.stale.domain.core.ErrorType
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.payment.GetPaymentUseCase
import ru.iandreyshev.stale.presentation.utils.SingleStateViewModel

class PaymentViewModel(
    id: PaymentId,
    name: String,
    private val calcResult: CalcResultUseCase,
    private val getPayment: GetPaymentUseCase
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
            val payment = getPayment(getState().id) ?: run {
                event { Event.Exit(ErrorType.Unknown) }
                return@launch
            }
            val transactions = payment.transactions
            val result = calcResult(transactions)

            modifyState {
                copy(
                    name = payment.name,
                    creationDate = payment.creationDate,
                    history = transactions,
                    result = result,
                    isLoading = false
                )
            }
        }
    }

    fun onAddTransaction() {
        event { Event.OpenTransactionEditor(getState().id) }
    }

    fun onExit() {
        event { Event.Exit() }
    }

}
