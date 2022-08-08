package ru.iandreyshev.stale.presentation.paymentsList

import ru.iandreyshev.stale.domain.payment.Payment
import ru.iandreyshev.stale.domain.payment.PaymentId
import ru.iandreyshev.stale.presentation.utils.SingleStateViewModel

class PaymentsListViewModel : SingleStateViewModel<State, Event>(
    initialState = State.default()
) {

    init {
        modifyState {
            copy(payments = listOf(
                Payment(PaymentId("0"), "Поездка в Сочи"),
                Payment(PaymentId("0"), "Гуляли по Нижнему новгороду")
            ))
        }
    }

    fun onOpenPayment(id: PaymentId) {
    }

    fun onAddTransaction(id: PaymentId) {
    }

    fun onEditPayment(id: PaymentId?) {
        event(NavigateToPaymentEditor())
    }

    fun onArchivePayment(id: PaymentId) {
    }

    fun onDeletePayment(id: PaymentId) {
    }

}
