package ru.iandreyshev.stale.presentation.paymentEditor

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iandreyshev.stale.domain.core.ErrorType
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.Result
import ru.iandreyshev.stale.domain.paymentEditor.GetPaymentDraftUseCase
import ru.iandreyshev.stale.domain.paymentEditor.PaymentDraftError
import ru.iandreyshev.stale.domain.paymentEditor.SavePaymentUseCase
import ru.iandreyshev.stale.domain.paymentEditor.ValidateMemberUseCase
import ru.iandreyshev.stale.presentation.utils.SingleStateViewModel

class PaymentEditorViewModel(
    id: PaymentId?,
    private val getDraft: GetPaymentDraftUseCase,
    private val savePayment: SavePaymentUseCase,
    private val isMemberValid: ValidateMemberUseCase
) : SingleStateViewModel<State, Event>(
    initialState = State.default(id = id)
) {

    init {
        loadPaymentOnStart()
    }

    fun onDraftChanged(uiDraft: UIPaymentDraft) {
        val newDraft = getState().draft.copy(
            name = uiDraft.name
        )

        modifyState { copy(draft = newDraft, memberCandidate = uiDraft.member) }
    }

    fun onAddMember() {
        val memberCandidateName = getState().memberCandidate.trim()
        val memberCandidate = Member(memberCandidateName)

        if (isMemberValid(memberCandidate)) {
            val newMembers = getState().draft.members.toMutableList() + memberCandidate
            val newDraft = getState().draft.copy(members = newMembers)
            modifyState { copy(draft = newDraft) }
            event { Event.ClearMemberField }
            return
        }

        event {
            val errorType = ErrorType.InvalidPaymentDraft(listOf(PaymentDraftError.INVALID_MEMBER))
            Event.ShowError(errorType)
        }
    }

    fun onRemoveMember(position: Int) {
        val members = getState().draft.members.toMutableList()
        members.removeAt(position)
        val newDraft = getState().draft.copy(members = members)
        modifyState { copy(draft = newDraft) }
    }

    fun onSave() {
        modifyState { copy(isSavingInProgress = true) }
        viewModelScope.launch {
            when (val result = savePayment(getState().draft)) {
                is Result.Success -> event {
                    Event.NavigateToPayment(result.data.id, result.data.name)
                }
                is Result.Error -> event(Event.ShowError(result.error))
            }
            modifyState { copy(isSavingInProgress = false) }
        }
    }

    fun onExit() {
        event(Event.Back)
    }

    private fun loadPaymentOnStart() {
        modifyState { copy(isLoading = true) }
        viewModelScope.launch {
            val id = getState().draft.id
            val draft = getDraft(id) ?: run {
                event(Event.BackWithError)
                return@launch
            }

            modifyState {
                copy(draft = draft, isLoading = false)
            }
        }
    }

}
