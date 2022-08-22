package ru.iandreyshev.payfriends.presentation.billEditor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.core.*
import ru.iandreyshev.payfriends.domain.computationEditor.ValidateMemberUseCase
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.billEditor.FilterMembers
import ru.iandreyshev.payfriends.domain.billEditor.SaveBillUseCase
import ru.iandreyshev.payfriends.system.Dispatchers

class Executor(
    private val dispatchers: Dispatchers,
    private val storage: Storage,
    private val filterMembers: FilterMembers,
    private val validateMember: ValidateMemberUseCase,
    private val saveBill: SaveBillUseCase
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
            is Intent.OnProducerSelected -> onBackerSelected(intent.producer, getState)
            is Intent.OnProducerCandidateSelected -> onProducerCandidateSelected(intent.query, getState)
            Intent.OnRemoveProducer -> onBackerSelected(null, getState)
            // Receiver field
            is Intent.OnReceiverFieldChanged -> onReceiverFieldChanged(intent.query, getState)
            is Intent.OnReceiverSelected -> onAddNewPayment(intent.receiver, getState)
            is Intent.OnReceiverCandidateSelected -> onReceiverCandidateSelected(intent.query, getState)
            // Transactions
            is Intent.OnCostChanged -> onCostChanged(intent.position, intent.cost, getState)
            is Intent.OnDescriptionChanged -> TODO()
            is Intent.OnRemovePayment -> onPaymentRemoved(intent.position, getState)
        }
    }

    private fun onStart(action: Action.OnStart, getState: () -> State) {
        scope.launch {
            val payment = withContext(dispatchers.io) { storage.get(action.computationId) }
                ?: run {
                    publish(Label.Exit("Payment not found"))
                    return@launch
                }
            val bill = payment.bills
                .firstOrNull { it.id == action.billId }
            val backer = bill?.backer
            val totalCost = bill?.payments.orEmpty().sumOf { it.cost }
            val newPaymentsReceiverSuggestions = when (backer) {
                null -> payment.members
                else -> payment.members
                    .toMutableList().apply {
                        removeAll { it.name == backer.name }
                    }
            }

            dispatch(
                Message.Started(
                    getState().copy(
                        billId = bill?.id ?: BillId.none(),
                        backerField = getState().backerField.copy(
                            backer = backer,
                            suggestions = payment.members,
                            cost = totalCost,
                        ),
                        receiverField = getState().receiverField.copy(
                            isCandidateActive = action.billId == null,
                            suggestions = newPaymentsReceiverSuggestions
                        ),
                        payments = bill?.payments.orEmpty(),
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
            when (saveBill(getState().composeBillDraft())) {
                is Result.Success -> publish(Label.Exit("Saving success"))
                is Result.Error -> publish(Label.Error.Unknown)
            }
            dispatch(Message.ChangeSavingState(false))
        }
    }

    private fun onBack(getState: () -> State) {
        when (getState().payments.isEmpty()) {
            true -> publish(Label.Exit())
            else -> publish(Label.ExitWithWarning)
        }
    }

    private fun onProducerFieldChanged(query: String, getState: () -> State) {
        val message = when (getState().backerField.backer) {
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
            validateMember(producer) -> onBackerSelected(producer, getState)
            else -> publish(Label.Error.InvalidProducerCandidate)
        }
    }

    private fun onBackerSelected(backer: Member?, getState: () -> State) {
        if (backer == null) {
            val query = getState().receiverField.candidateQuery
            val suggestions = getProducerSuggestions(query, null, getState().members)
            dispatch(Message.UpdateBacker(null, suggestions))
            return
        }

        val query = getState().receiverField.candidateQuery
        val suggestions = getProducerSuggestions(query, backer, getState().members)
        dispatch(Message.UpdateBacker(backer, suggestions))
    }

    private fun onReceiverFieldChanged(query: String, getState: () -> State) {
        var candidate = query.trim()
        val producer = getState().backerField.backer
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

    private fun onAddNewPayment(receiver: Member, getState: () -> State) {
        val newPayment = Payment.empty(receiver = receiver)
        val payments = getState().payments.toMutableList()
        payments.add(newPayment)
        dispatch(Message.UpdatePayments(payments, getState().backerField.cost))
    }

    private fun onReceiverCandidateSelected(query: String, getState: () -> State) {
        val receiver = Member(query.trim())
        when {
            validateMember(receiver) -> onAddNewPayment(receiver, getState)
            else -> publish(Label.Error.InvalidProducerCandidate)
        }
    }

    private fun onCostChanged(position: Int, costStr: String, getState: () -> State) {
        val payments = getState().payments.toMutableList()
        val cost = when {
            costStr.isEmpty() -> 0
            else -> costStr.toInt()
        }
        val payment = payments[position].copy(cost = cost)
        payments.removeAt(position)
        payments.add(position, payment)
        val totalCost = payments.sumOf { it.cost }
        dispatch(Message.UpdatePayments(payments, totalCost))
    }

    private fun onPaymentRemoved(position: Int, getState: () -> State) {
        val payments = getState().payments.toMutableList()
        payments.removeAt(position)
        val totalCost = payments.sumOf { it.cost }
        dispatch(Message.UpdatePayments(payments, totalCost))
    }

}
