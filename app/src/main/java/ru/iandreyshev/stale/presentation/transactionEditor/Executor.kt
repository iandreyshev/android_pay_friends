package ru.iandreyshev.stale.presentation.transactionEditor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iandreyshev.stale.domain.core.*
import ru.iandreyshev.stale.domain.paymentEditor.ValidateMemberUseCase
import ru.iandreyshev.stale.domain.payments.PaymentsStorage
import ru.iandreyshev.stale.domain.transactionEditor.FilterMembers
import ru.iandreyshev.stale.domain.transactionEditor.SaveTransactionsUseCase
import ru.iandreyshev.stale.system.Dispatchers

class Executor(
    private val dispatchers: Dispatchers,
    private val storage: PaymentsStorage,
    private val filterMembers: FilterMembers,
    private val validateMember: ValidateMemberUseCase,
    private val saveTransactions: SaveTransactionsUseCase
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
            Intent.OnBack -> onBack(getState)
            // Producer field
            is Intent.OnProducerFieldChanged -> onProducerFieldChanged(intent.query, getState)
            is Intent.OnProducerSelected -> onProducerSelected(intent.producer, getState)
            is Intent.OnProducerCandidateSelected -> onProducerCandidateSelected(intent.query, getState)
            Intent.OnRemoveProducer -> onProducerSelected(null, getState)
            // Receiver field
            is Intent.OnReceiverFieldChanged -> onReceiverFieldChanged(intent.query, getState)
            is Intent.OnReceiverSelected -> onReceiverSelected(intent.receiver, getState)
            is Intent.OnReceiverCandidateSelected -> onReceiverCandidateSelected(intent.query, getState)
            // Transactions
            is Intent.OnCostChanged -> onCostChanged(intent.position, intent.cost, getState)
            is Intent.OnDescriptionChanged -> TODO()
            is Intent.OnRemoveTransaction -> onTransactionRemoved(intent.position, getState)
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
                        transactionId = action.transactionId ?: TransactionId.none(),
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
        dispatch(Message.ChangeSavingState(true))
        scope.launch {
            when (saveTransactions(getState().transactions)) {
                is Result.Success -> publish(Label.Exit("Saving success"))
                is Result.Error -> publish(Label.Error.Unknown)
            }
            dispatch(Message.ChangeSavingState(false))
        }
    }

    private fun onBack(getState: () -> State) {
        when (getState().transactions.isEmpty()) {
            true -> publish(Label.Exit())
            else -> publish(Label.ExitWithWarning)
        }
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

    private fun onProducerCandidateSelected(query: String, getState: () -> State) {
        val producer = Member(query.trim())

        when {
            validateMember(producer) -> onProducerSelected(producer, getState)
            else -> publish(Label.Error.InvalidProducerCandidate)
        }
    }

    private fun onProducerSelected(producer: Member?, getState: () -> State) {
        if (producer == null) {
            val query = getState().receiverField.candidateQuery
            val suggestions = getProducerSuggestions(query, null, getState().members)
            dispatch(Message.UpdateProducer(null, suggestions))
            return
        }

        val query = getState().receiverField.candidateQuery
        val suggestions = getProducerSuggestions(query, producer, getState().members)
        dispatch(Message.UpdateProducer(producer, suggestions))
    }

    private fun onReceiverFieldChanged(query: String, getState: () -> State) {
        var candidate = query.trim()
        val producer = getState().producerField.producer
        val suggestions = getProducerSuggestions(query, producer, getState().members)
        candidate = if (suggestions.isEmpty()) candidate else ""
        dispatch(Message.UpdateReceiverSuggestions(query, candidate, suggestions))
    }

    private fun getProducerSuggestions(candidate: String, producer: Member?, members: List<Member>): List<Member> {
        val filters = FilterMembers.Filters(candidate.trim())
        val newMembers = when (producer) {
            null -> members
            else -> members - producer
        }

        return filterMembers(newMembers, filters)
    }

    private fun onReceiverSelected(receiver: Member, getState: () -> State) {
        val producer = getState().producerField.producer ?: Member("")
        val participants = TransactionParticipants(producer, receiver)
        val newTransaction = Transaction.empty(participants = participants)
        val transactions = getState().transactions.toMutableList()
        transactions.add(newTransaction)
        dispatch(Message.UpdateTransactions(transactions, getState().producerField.cost))
    }

    private fun onReceiverCandidateSelected(query: String, getState: () -> State) {
        val receiver = Member(query.trim())
        when {
            validateMember(receiver) -> onReceiverSelected(receiver, getState)
            else -> publish(Label.Error.InvalidProducerCandidate)
        }
    }

    private fun onCostChanged(position: Int, costStr: String, getState: () -> State) {
        val transactions = getState().transactions.toMutableList()
        val cost = when {
            costStr.isEmpty() -> 0
            else -> costStr.toInt()
        }
        val transaction = transactions[position].copy(cost = cost)
        transactions.removeAt(position)
        transactions.add(position, transaction)
        val totalCost = transactions.sumOf { it.cost }
        dispatch(Message.UpdateTransactions(transactions, totalCost))
    }

    private fun onTransactionRemoved(position: Int, getState: () -> State) {
        val transactions = getState().transactions.toMutableList()
        transactions.removeAt(position)
        val totalCost = transactions.sumOf { it.cost }
        dispatch(Message.UpdateTransactions(transactions, totalCost))
    }

}
