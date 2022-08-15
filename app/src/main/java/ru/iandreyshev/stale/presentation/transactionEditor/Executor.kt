package ru.iandreyshev.stale.presentation.transactionEditor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iandreyshev.stale.domain.core.TransactionId
import ru.iandreyshev.stale.domain.payments.PaymentsStorage
import ru.iandreyshev.stale.domain.transactionEditor.FilterMembers
import ru.iandreyshev.stale.system.Dispatchers

class Executor(
    private val dispatchers: Dispatchers,
    private val storage: PaymentsStorage,
    private val filterMembers: FilterMembers
) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

    override fun executeAction(action: Action, getState: () -> State) {
        when (action) {
            is Action.OnStart -> onStart(action, getState)
        }
    }

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.OnProducerFieldChanged -> onProducerFieldChanged(intent.text, getState)
        }
    }

    private fun onStart(action: Action.OnStart, getState: () -> State) {
        scope.launch {
            val payment = withContext(dispatchers.io) { storage.get(action.paymentId) }
                ?: run {
                    publish(Label.Exit("Payment not found"))
                    return@launch
                }
            val transaction = payment.transactions
                .firstOrNull { it.id == action.transactionId }
            val producer = transaction?.participants?.producer
            val excludedMember = listOfNotNull(producer?.name)
            val filters = FilterMembers.Filters(exclude = excludedMember)
            val producerSuggestions = filterMembers(payment.members, filters)
            val totalCost = payment.transactions.sumOf { it.cost }
            val newTransactionReceiverSuggestions = when (producer) {
                null -> payment.members
                else -> payment.members
                    .toMutableList().apply {
                        removeAll { it.name == producer.name }
                    }
            }

            Message.Started(
                getState().copy(
                    paymentId = payment.id,
                    transactionId = action.transactionId ?: TransactionId(""),
                    producer = producer,
                    producerSuggestions = producerSuggestions,
                    totalCost = totalCost,
                    members = payment.members,
                    transactions = transaction?.let { listOf(it) }.orEmpty(),
                    isNewTransactionsAvailable = action.transactionId == null,
                    newTransactionReceiverSuggestions = newTransactionReceiverSuggestions
                )
            )
        }
    }

    private fun onProducerFieldChanged(text: String, getState: () -> State) {
        val excludedMember = mutableListOf<String>()
        getState().producer?.name?.let { member ->
            excludedMember.add(member)
        }
        val filters = FilterMembers.Filters(query = text, exclude = excludedMember)
        val producerSuggestions = filterMembers(getState().members, filters)
    }

}
