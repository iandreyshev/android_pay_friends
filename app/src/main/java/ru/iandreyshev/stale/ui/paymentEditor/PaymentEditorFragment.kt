package ru.iandreyshev.stale.ui.paymentEditor

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.iandreyshev.stale.App
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.FragmentPaymentEditorBinding
import ru.iandreyshev.stale.domain.core.ErrorType
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.paymentEditor.GetPaymentDraftUseCase
import ru.iandreyshev.stale.domain.paymentEditor.SavePaymentUseCase
import ru.iandreyshev.stale.domain.paymentEditor.ValidatePaymentDraftUseCase
import ru.iandreyshev.stale.presentation.paymentEditor.Event
import ru.iandreyshev.stale.presentation.paymentEditor.PaymentEditorViewModel
import ru.iandreyshev.stale.presentation.paymentEditor.State
import ru.iandreyshev.stale.presentation.paymentEditor.UIPaymentDraft
import ru.iandreyshev.stale.ui.utils.toast
import ru.iandreyshev.stale.ui.utils.uiLazy
import ru.iandreyshev.stale.ui.utils.viewBindings
import ru.iandreyshev.stale.ui.utils.viewModelFactory

class PaymentEditorFragment : Fragment(R.layout.fragment_payment_editor) {

    private val mArgs by navArgs<PaymentEditorFragmentArgs>()
    private val mBinding by viewBindings(FragmentPaymentEditorBinding::bind)
    private val mViewModel by viewModelFactory {
        PaymentEditorViewModel(
            id = mArgs.paymentId?.let { PaymentId(it) },
            getDraft = GetPaymentDraftUseCase(App.storage),
            savePayment = SavePaymentUseCase(
                validate = ValidatePaymentDraftUseCase(),
                storage = App.storage,
                dateProvider = App.dateProvider
            )
        )
    }
    private val mNavController by uiLazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAppBar()
        initNameField()
        initMembersField()

        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mViewModel.event.observe(viewLifecycleOwner, ::handleEvent)
    }

    private fun initAppBar() {
        mBinding.toolbar.setNavigationOnClickListener { mNavController.popBackStack() }
        mBinding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.paymentEditorMenuSave -> mViewModel.onSave()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun initNameField() {
        mBinding.nameField.doOnTextChanged { _, _, _, _ ->
            mViewModel.onDraftChanged(composeDraft())
        }
    }

    private fun initMembersField() {
        mBinding.membersField.doOnTextChanged { _, _, _, _ ->
            mViewModel.onDraftChanged(composeDraft())
        }
    }

    private fun render(state: State) {
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.NavigateToPayment ->
                PaymentEditorFragmentDirections
                    .actionBackToNewPayment(event.id.value)
                    .let(mNavController::navigate)
            is Event.ShowError -> when (val error = event.error) {
                is ErrorType.InvalidPaymentDraft -> when (error.errors.firstOrNull()) {
                    null -> Unit
                    else -> toast(R.string.payment_editor_error_empty_name)
                }
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
        members = mBinding.membersField.text.toString()
    )

}
