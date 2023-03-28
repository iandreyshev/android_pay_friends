package ru.iandreyshev.payfriends.presentation.billEditor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.domain.billEditor.BillDraft
import ru.iandreyshev.payfriends.domain.billEditor.FilterMembersUseCase
import ru.iandreyshev.payfriends.domain.billEditor.SaveBillUseCase
import ru.iandreyshev.payfriends.domain.computationEditor.ValidateMemberUseCase
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.*
import ru.iandreyshev.payfriends.system.Dispatchers

class Executor(
    private val dispatchers: Dispatchers,
    private val storage: Storage,
    private val filterMembers: FilterMembersUseCase,
    private val validateMember: ValidateMemberUseCase,
    private val saveBill: SaveBillUseCase,
    private val getDefaultTitle: DefaultBillTitleProvider
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
            // Title
            is Intent.OnTitleChanged -> onTitleChanged(intent.text)
            // Producer field
            is Intent.OnProducerFieldChanged -> onProducerFieldChanged(intent.query, getState)
            is Intent.OnProducerSelected -> onProducerSelected(intent.producer, getState)
            is Intent.OnProducerCandidateSelected -> onProducerCandidateSelected(intent.query, getState)
            is Intent.OnCommonBillToggle -> onCommonBillToggle(intent.isTurnOn, getState)
            is Intent.OnCommonBillForUserToggle -> onCommonBillForUserToggle(intent.isTurnOn, getState)
            is Intent.OnCommonBillCostChanged -> onCommonBillCostChanged(intent.cost, getState)
            is Intent.OnCommonBillRemoveMember -> onCommonBillRemoveMember(intent.member, getState)
            Intent.OnRemoveProducer -> onProducerSelected(null, getState)
            // Receiver field
            is Intent.OnReceiverFieldChanged -> onReceiverFieldChanged(intent.query, getState)
            is Intent.OnReceiverSelected -> onReceiverSelected(intent.receiver, getState)
            is Intent.OnReceiverCandidateSelected -> onReceiverCandidateSelected(intent.query, getState)
            // Transactions
            is Intent.OnCostChanged -> onCostChanged(intent.position, intent.cost, getState)
            is Intent.OnDescriptionChanged -> onDescriptionChanged(intent.position, intent.description, getState)
            is Intent.OnRemovePayment -> onPaymentRemoved(intent.position, getState)
        }
    }

    private fun onStart(action: Action.OnStart, getState: () -> State) {
        scope.launch {
            val computation = withContext(dispatchers.io) { storage.get(action.computationId) }
                ?: run {
                    publish(Label.Exit("Payment not found"))
                    return@launch
                }
            val bill = computation.bills
                .firstOrNull { it.id == action.billId }
            val producer = bill?.producer
            val totalCost = bill?.payments.orEmpty().sumOf { it.cost }
            val newPaymentsReceiverSuggestions = when (producer) {
                null -> computation.members
                else -> computation.members
                    .toMutableList().apply {
                        removeAll { it.name == producer.name }
                    }
            }

            dispatch(
                Message.Started(
                    getState().copy(
                        billId = bill?.id ?: BillId.none(),
                        number = computation.bills.count() + 1,
                        producerField = getState().producerField.copy(
                            producer = producer,
                            suggestions = computation.members,
                            cost = totalCost,
                        ),
                        receiverField = getState().receiverField.copy(
                            isCandidateActive = action.billId == null,
                            suggestions = newPaymentsReceiverSuggestions
                        ),
                        payments = bill?.payments.orEmpty(),
                        members = computation.members,
                        isStarted = true
                    )
                )
            )
        }
    }

    private fun onSave(getState: () -> State) {
        dispatch(Message.ChangeSavingState(true))
        scope.launch {
            when (val result = saveBill(getState().composeBillDraft())) {
                is Result.Success -> publish(Label.Exit("Saving success"))
                is Result.Error -> publish(
                    when (result.error) {
                        ErrorType.InvalidCost -> Label.Error.InvalidCost
                        is ErrorType.InvalidPaymentDraft,
                        ErrorType.Unknown -> Label.Error.Unknown
                    }
                )
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

    private fun onTitleChanged(title: String) {
        dispatch(Message.ChangeTitle(title))
    }

    private fun onProducerFieldChanged(query: String, getState: () -> State) {
        val message = when (getState().producerField.producer) {
            null -> {
                var candidate = query.trim()
                val filters = FilterMembersUseCase.Filters(candidate)
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

    private fun onCommonBillToggle(isTurnOn: Boolean, getState: () -> State) {
        val state = getState()

        when {
            isTurnOn -> {
                val cost = state.payments.firstOrNull()?.cost ?: 0
                val newCommonBill = state.commonBill
                    .copy(isTurnedOn = true, isUserInBill = true, cost = cost, members = emptyList())
                dispatch(Message.UpdateCommonBill(newCommonBill))
                dispatch(Message.UpdatePayments(emptyList(), cost))
            }
            else -> {
                val newCommonBill = state.commonBill.copy(isTurnedOn = false, cost = 0)
                dispatch(Message.UpdateCommonBill(newCommonBill))
                dispatch(Message.UpdatePayments(emptyList(), 0))
            }
        }
    }

    private fun onCommonBillForUserToggle(isTurnOn: Boolean, getState: () -> State) {
        val newCommonBill = getState().commonBill.copy(isUserInBill = isTurnOn)
        dispatch(Message.UpdateCommonBill(newCommonBill))
    }

    private fun onCommonBillCostChanged(cost: Int, getState: () -> State) {
        val newCommonBill = getState().commonBill.copy(cost = cost)
        dispatch(Message.UpdateCommonBill(newCommonBill))
    }

    private fun onCommonBillRemoveMember(member: Member, getState: () -> State) {
        val commonBill = getState().commonBill
        val newMembers = commonBill.members.toMutableList()
            .apply { remove(member) }
        val newCommonBill = commonBill.copy(members = newMembers)
        dispatch(Message.UpdateCommonBill(newCommonBill))
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
        val filters = FilterMembersUseCase.Filters(candidate.trim())
        val newMembers = when (producer) {
            null -> members
            else -> members - producer
        }

        return filterMembers(newMembers, filters)
    }

    private fun onReceiverSelected(receiver: Member, getState: () -> State) {
        when {
            getState().isCommonBill -> {
                val currentBill = getState().commonBill
                val newMembers = currentBill.members.toMutableList()
                    .apply { this += receiver }
                val newBill = currentBill.copy(members = newMembers)

                dispatch(Message.UpdateCommonBill(newBill))
            }
            else -> {
                val newPayment = Payment.empty(receiver = receiver)
                val payments = getState().payments.toMutableList()
                payments.add(newPayment)
                dispatch(Message.UpdatePayments(payments, getState().producerField.cost))
                dispatch(Message.UpdateCommonBill(getState().commonBill.copy(isEnabled = payments.count() <= 1)))
                publish(Label.ScrollToBottom)
            }
        }
    }

    private fun onReceiverCandidateSelected(query: String, getState: () -> State) {
        val receiver = Member(query.trim())
        when {
            validateMember(receiver) -> onReceiverSelected(receiver, getState)
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

    private fun onDescriptionChanged(position: Int, description: String, getState: () -> State) {
        val payments = getState().payments.toMutableList()
        val payment = payments[position].copy(description = description)
        payments.removeAt(position)
        payments.add(position, payment)
        dispatch(Message.UpdatePayments(payments, getState().producerField.cost))
    }

    private fun onPaymentRemoved(position: Int, getState: () -> State) {
        val payments = getState().payments.toMutableList()
        payments.removeAt(position)
        val totalCost = payments.sumOf { it.cost }
        dispatch(Message.UpdatePayments(payments, totalCost))

        val newCommonBillState = getState().commonBill
            .copy(isEnabled = payments.count() <= 1)
        dispatch(Message.UpdateCommonBill(newCommonBillState))
    }

    private fun State.composeBillDraft(): BillDraft {
        val title = when {
            title.isBlank() -> getDefaultTitle(number)
            else -> title
        }

        return BillDraft(
            computationId = computationId,
            id = billId,
            title = title,
            producer = producerField.producer,
            payments = payments
        )
    }

}
