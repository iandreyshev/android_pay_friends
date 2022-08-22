package ru.iandreyshev.payfriends.presentation.computationEditor

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.Member
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.core.Result
import ru.iandreyshev.payfriends.domain.computationEditor.GetComputationDraftUseCase
import ru.iandreyshev.payfriends.domain.computationEditor.PaymentDraftError
import ru.iandreyshev.payfriends.domain.computationEditor.SaveComputationUseCase
import ru.iandreyshev.payfriends.domain.computationEditor.ValidateMemberUseCase
import ru.iandreyshev.payfriends.presentation.utils.SingleStateViewModel

class ComputationEditorViewModel(
    id: ComputationId?,
    private val getDraft: GetComputationDraftUseCase,
    private val saveComputation: SaveComputationUseCase,
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

        if (!isMemberValid(memberCandidate)) {
            event {
                val errorType = ErrorType.InvalidPaymentDraft(listOf(PaymentDraftError.INVALID_MEMBER))
                Event.ShowError(errorType)
            }
            return
        } else if (getState().draft.members.contains(memberCandidate)) {
            event {
                val errorType = ErrorType.InvalidPaymentDraft(listOf(PaymentDraftError.MEMBER_EXISTS))
                Event.ShowError(errorType)
            }
            return
        }

        val newMembers = getState().draft.members.toMutableList() + memberCandidate
        val newDraft = getState().draft.copy(members = newMembers)
        modifyState { copy(draft = newDraft) }
        event { Event.ClearMemberField }
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
            when (val result = saveComputation(getState().draft)) {
                is Result.Success -> event {
                    Event.NavigateToPayment(result.data.id, result.data.title)
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
