package ru.iandreyshev.stale.presentation.transactionEditor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            Action.InvokeOnStart -> onStart(getState)
        }
    }

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.OnProducerFieldChanged -> onProducerFieldChanged(intent.text, getState)
        }
    }

    private fun onStart(getState: () -> State) {
        scope.launch {
            val paymentId = getState().paymentId
            val members = withContext(dispatchers.io) { storage.getMembers(paymentId) }

            val excludedMember = mutableListOf<String>()
            getState().producer?.name?.let { member ->
                excludedMember.add(member)
            }
            val filters = FilterMembers.Filters(exclude = excludedMember)
            val producerSuggestions = filterMembers(members, filters)

            Message.InitComplete(
                getState().copy(
                    producerSuggestions = producerSuggestions
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
