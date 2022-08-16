package ru.iandreyshev.stale.presentation.transactionEditor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.Transaction
import ru.iandreyshev.stale.domain.core.TransactionId
import ru.iandreyshev.stale.domain.core.TransactionParticipants
import ru.iandreyshev.stale.domain.paymentEditor.ValidateMemberUseCase
import ru.iandreyshev.stale.domain.payments.PaymentsStorage
import ru.iandreyshev.stale.domain.transactionEditor.FilterMembers
import ru.iandreyshev.stale.system.Dispatchers

class Executor(
    private val dispatchers: Dispatchers,
    private val storage: PaymentsStorage,
    private val filterMembers: FilterMembers,
    private val validateMember: ValidateMemberUseCase
) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

    override fun executeAction(action: Action, getState: () -> State) {
        when (action) {
            is Action.OnStart -> onStart(action, getState)
        }
    }

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            // Common
            Intent.OnSave -> onSave(getState)
            Intent.OnExit -> publish(Label.Exit())
            // Producer field
            is Intent.OnProducerFieldChanged -> onProducerFieldChanged(intent.query, getState)
            is Intent.OnProducerSelected -> dispatch(Message.UpdateProducer(intent.producer))
            is Intent.OnProducerCandidateSelected -> onProducerCandidateSelected(intent.query)
            Intent.OnRemoveProducer -> dispatch(Message.UpdateProducer(null))
            // Receiver field
            is Intent.OnReceiverFieldChanged -> onReceiverFieldChanged(intent.query, getState)
            is Intent.OnReceiverSelected -> onReceiverSelected(intent.receiver, getState)
            is Intent.OnReceiverCandidateSelected -> onReceiverCandidateSelected(intent.query, getState)
            // Transactions
            is Intent.OnCostChanged -> TODO()
            is Intent.OnDescriptionChanged -> TODO()
            is Intent.OnRemoveTransaction -> TODO()
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
            val totalCost = payment.transactions.sumOf { it.cost }
            val newTransactionReceiverSuggestions = when (producer) {
                null -> payment.members
                else -> payment.members
                    .toMutableList().apply {
                        removeAll { it.name == producer.name }
                    }
            }

            dispatch(
                Message.Started(
                    getState().copy(
                        paymentId = payment.id,
                        transactionId = action.transactionId ?: TransactionId(""),
                        producerField = getState().producerField.copy(
                            producer = producer,
                            suggestions = payment.members,
                            cost = totalCost,
                        ),
                        receiverField = getState().receiverField.copy(
                            isCandidateActive = action.transactionId == null,
                            suggestions = newTransactionReceiverSuggestions
                        ),
                        transactions = listOfNotNull(transaction),
                        members = payment.members,
                        isStarted = true
                    )
                )
            )
        }
    }

    private fun onSave(getState: () -> State) {
    }

    private fun onProducerFieldChanged(query: String, getState: () -> State) {
        val message = when (getState().producerField.producer) {
            null -> {
                var candidate = query.trim()
                val filters = FilterMembers.Filters(candidate)
                val members = filterMembers(getState().members, filters)
                candidate = if (members.isEmpty()) candidate else ""
                Message.UpdateProducerSuggestions(query, candidate, members)
            }
            else -> Message.UpdateProducerSuggestions(query, "", emptyList())
        }
        dispatch(message)
    }

    private fun onProducerCandidateSelected(query: String) {
        val producer = Member(query.trim())

        when {
            validateMember(producer) -> dispatch(Message.UpdateProducer(producer))
            else -> publish(Label.Error.InvalidProducerCandidate)
        }
    }

    private fun onReceiverFieldChanged(query: String, getState: () -> State) {
        val message = when (getState().receiverField.receiver) {
            null -> {
                var candidate = query.trim()
                val filters = FilterMembers.Filters(candidate)
                val members = filterMembers(getState().members, filters)
                candidate = if (members.isEmpty()) candidate else ""
                Message.UpdateReceiverSuggestions(query, candidate, members)
            }
            else -> Message.UpdateReceiverSuggestions(query, "", emptyList())
        }
        dispatch(message)
    }

    private fun onReceiverSelected(receiver: Member, getState: () -> State) {
        val producer = getState().producerField.producer ?: Member("")
        val participants = TransactionParticipants(producer, receiver)
        val newTransaction = Transaction.empty(participants = participants)
        val transactions = getState().transactions.toMutableList()
        transactions.add(newTransaction)
        dispatch(Message.UpdateTransactions(transactions))
    }

    private fun onReceiverCandidateSelected(query: String, getState: () -> State) {
        val receiver = Member(query.trim())
        when {
            validateMember(receiver) -> onReceiverSelected(receiver, getState)
            else -> publish(Label.Error.InvalidProducerCandidate)
        }
    }

}
