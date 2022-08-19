package ru.iandreyshev.payfriends.ui.paymentEditor

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import ru.iandreyshev.payfriends.App
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentPaymentEditorBinding
import ru.iandreyshev.payfriends.domain.core.ErrorType
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.paymentEditor.GetPaymentDraftUseCase
import ru.iandreyshev.payfriends.domain.paymentEditor.SavePaymentUseCase
import ru.iandreyshev.payfriends.domain.paymentEditor.ValidateMemberUseCase
import ru.iandreyshev.payfriends.domain.paymentEditor.ValidatePaymentDraftUseCase
import ru.iandreyshev.payfriends.presentation.paymentEditor.Event
import ru.iandreyshev.payfriends.presentation.paymentEditor.PaymentEditorViewModel
import ru.iandreyshev.payfriends.presentation.paymentEditor.State
import ru.iandreyshev.payfriends.presentation.paymentEditor.UIPaymentDraft
import ru.iandreyshev.payfriends.ui.members.MembersAdapter
import ru.iandreyshev.payfriends.ui.utils.*


class PaymentEditorFragment : Fragment(R.layout.fragment_payment_editor) {

    private val mArgs by navArgs<PaymentEditorFragmentArgs>()
    private val mBinding by viewBindings(FragmentPaymentEditorBinding::bind)
    private val mViewModel by viewModelFactory {
        PaymentEditorViewModel(
            id = mArgs.paymentId?.let { PaymentId(it) },
            getDraft = GetPaymentDraftUseCase(App.storage),
            savePayment = SavePaymentUseCase(
                isDraftValid = ValidatePaymentDraftUseCase(
                    isMemberValid = ValidateMemberUseCase()
                ),
                storage = App.storage,
                dateProvider = App.dateProvider
            ),
            isMemberValid = ValidateMemberUseCase()
        )
    }
    private val mNavController by uiLazy { findNavController() }
    private val mMembersAdapter by uiLazy {
        MembersAdapter(
            onRemoveMemberClick = { _, position -> mViewModel.onRemoveMember(position) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAppBar()
        initNameField()
        initMembersField()
        initAddMemberButton()

        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mViewModel.event.observe(viewLifecycleOwner, ::handleEvent)

        if (savedInstanceState == null) {
            mBinding.nameField.showKeyboard()
        }
    }

    private fun initAppBar() {
        mBinding.toolbar.setNavigationOnClickListener { mViewModel.onExit() }
        mBinding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.paymentEditorMenuSave -> mViewModel.onSave()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun initNameField() {
        mBinding.nameField.doAfterTextChanged { mViewModel.onDraftChanged(composeDraft()) }
    }

    private fun initMembersField() {
        mBinding.memberField.doAfterTextChanged { mViewModel.onDraftChanged(composeDraft()) }
        mBinding.memberField.setOnEditorActionListener { _, actionId, event ->
            if (event?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                mBinding.addMemberButton.clickableArea.performClick()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun initAddMemberButton() {
        mBinding.addMemberButton.clickableArea.setOnClickListener { mViewModel.onAddMember() }
    }

    private fun render(state: State) {
        mBinding.membersList.isVisible = state.draft.members.isNotEmpty()
        if (mBinding.membersList.adapter == null) {
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            mBinding.membersList.layoutManager = layoutManager
            mBinding.membersList.adapter = mMembersAdapter
            mBinding.membersList.addItemDecoration(MemberItemDecoration(resources))
            mBinding.membersList.itemAnimator = null
        }
        mMembersAdapter.submitList(state.draft.members)

        mBinding.addMemberButton.content.isVisible = state.canAddMember
        mBinding.addMemberButton.memberCandidate.text = state.memberCandidate
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.NavigateToPayment ->
                PaymentEditorFragmentDirections
                    .actionBackToNewPayment(event.id.value, event.name)
                    .let(mNavController::navigate)
            Event.ClearMemberField ->
                mBinding.memberField.setText("")
            is Event.ShowError -> when (val error = event.error) {
                is ErrorType.InvalidPaymentDraft -> when (error.errors.firstOrNull()) {
                    null -> Unit
                    else -> toast(R.string.payment_editor_error_empty_name)
                }
                ErrorType.Unknown -> toast(R.string.common_unknown_error)
            }
            is Event.BackWithError -> {
                toast(R.string.common_unknown_error)
                mNavController.popBackStack()
            }
            Event.Back -> mNavController.popBackStack()
        }
    }

    private fun composeDraft() = UIPaymentDraft(
        name = mBinding.nameField.text.toString(),
        member = mBinding.memberField.text.toString()
    )

}
