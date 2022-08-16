package ru.iandreyshev.stale.presentation.transactionEditor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.TransactionId
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
            Intent.OnSave -> TODO()
            Intent.OnExit -> TODO()
            is Intent.OnProducerFieldChanged -> onProducerFieldChanged(intent.text, getState)
            is Intent.OnProducerSelected -> dispatch(Message.UpdateProducer(intent.member))
            is Intent.OnProducerCandidateSelected -> onProducerCandidateSelected(intent.name)
            Intent.OnRemoveProducer -> dispatch(Message.UpdateProducer(null))
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

    private fun onProducerFieldChanged(text: String, getState: () -> State) {
        dispatch(
            when (getState().producerField.producer) {
                null -> {
                    val candidate = text.trim()
                    val filters = FilterMembers.Filters(candidate)
                    val members = filterMembers(getState().members, filters)
                    Message.UpdateProducerSuggestions(
                        suggestions = members,
                        candidate = when {
                            members.isEmpty() -> candidate
                            else -> ""
                        }
                    )
                }
                else -> Message.UpdateProducerSuggestions(
                    suggestions = emptyList(),
                    candidate = ""
                )
            }
        )
    }

    private fun onProducerCandidateSelected(candidate: String) {
        val member = Member(candidate.trim())

        when {
            validateMember(member) -> dispatch(Message.UpdateProducer(member))
            else -> publish(Label.Error.InvalidProducerCandidate)
        }
    }

}
