package ru.iandreyshev.stale.ui.paymentsList

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.iandreyshev.stale.App
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.FragmentPaymentsListBinding
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.payments.GetPaymentsListUseCase
import ru.iandreyshev.stale.domain.payments.MarkPaymentAsCompletedUseCase
import ru.iandreyshev.stale.presentation.paymentsList.Event
import ru.iandreyshev.stale.presentation.paymentsList.PaymentsListViewModel
import ru.iandreyshev.stale.presentation.paymentsList.State
import ru.iandreyshev.stale.ui.utils.dismissOnDestroy
import ru.iandreyshev.stale.ui.utils.uiLazy
import ru.iandreyshev.stale.ui.utils.viewBindings
import ru.iandreyshev.stale.ui.utils.viewModelFactory

class PaymentListFragment : Fragment(R.layout.fragment_payments_list) {

    private val mBinding by viewBindings(FragmentPaymentsListBinding::bind)
    private val mViewModel by viewModelFactory {
        PaymentsListViewModel(
            isListOfActivePayments = mArgs,
            storage = App.storage,
            completePayment = MarkPaymentAsCompletedUseCase(storage = App.storage),
            getList = GetPaymentsListUseCase(storage = App.storage)
        )
    }
    private val mArgs by uiLazy { arguments?.getBoolean(ARG_IS_ACTIVE, true) ?: true }
    private val mAdapter by uiLazy {
        PaymentsAdapter(
            isActivePayments = mArgs,
            onClick = mViewModel::onOpenPayment,
            onAddTransaction = mViewModel::onAddTransaction,
            onOptionsMenuOpen = ::onOptionsMenuOpen
        )
    }
    private val mNavController by uiLazy { findNavController() }
    private var mPopupMenu: PopupMenu? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initNewPaymentButton()
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mViewModel.event.observe(viewLifecycleOwner, ::handleEvent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPopupMenu.dismissOnDestroy()
    }

    private fun initView() {
        mBinding.listView.adapter = mAdapter
    }

    private fun initNewPaymentButton() {
        mBinding.newPaymentButton.setOnClickListener {
            mViewModel.onNewPayment()
        }
        mBinding.emptyViewButton.setOnClickListener {
            mViewModel.onNewPayment()
        }
    }

    private fun render(state: State) {
        if (state.isLoading) {
            mBinding.loadingIndicator.isVisible = true
            mBinding.emptyViewGroup.isVisible = false
            mBinding.newPaymentButton.isVisible = false
            return
        }

        mBinding.loadingIndicator.isVisible = false
        mBinding.emptyViewGroup.isVisible = state.payments.isEmpty()
        mBinding.emptyViewButton.isVisible = mBinding.emptyViewGroup.isVisible && state.isListOfActivePayments
        mBinding.listView.isVisible = state.payments.isNotEmpty()
        mBinding.newPaymentButton.isVisible = state.payments.isNotEmpty()
        mAdapter.submitList(state.payments)

        mBinding.toolbarTitle.text = when (state.isListOfActivePayments) {
            true -> getString(R.string.menu_item_active_payments_title)
            else -> getString(R.string.menu_item_completed_payments_title)
        }

        mBinding.emptyViewText.text = when (state.isListOfActivePayments) {
            true -> getString(R.string.payments_list_empty_view_text_active)
            else -> getString(R.string.common_empty_list)
        }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.NavigateToPayment ->
                PaymentListFragmentDirections
                    .actionOpenPayment(event.id.value, event.name)
                    .let(mNavController::navigate)
            is Event.NavigateToPaymentEditor ->
                PaymentListFragmentDirections
                    .actionEditPayment(event.id?.value)
                    .let(mNavController::navigate)
            is Event.NavigateToTransactionEditor ->
                PaymentListFragmentDirections
                    .actionAddTransactions(event.paymentId.value, null)
                    .let(mNavController::navigate)
            is Event.ShowPaymentDeletingDialog -> TODO()
        }
    }

    private fun onOptionsMenuOpen(view: View, id: PaymentId) {
        mPopupMenu = PopupMenu(requireContext(), view).apply {
            inflate(
                when (mArgs) {
                    true -> R.menu.menu_active_payment_list_options
                    else -> R.menu.menu_completed_payment_list_options
                }
            )
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.paymentOptionComplete -> mViewModel.onCompletePayment(id, isCompleted = true)
                    R.id.paymentOptionEdit -> mViewModel.onEditPayment(id)
                    R.id.paymentOptionDelete -> mViewModel.onDeletePayment(id)
                }
                return@setOnMenuItemClickListener true
            }
            show()
        }
    }

    companion object {
        private const val ARG_IS_ACTIVE = "is_active"

        fun args(isListOfActivePayments: Boolean) = Bundle().apply {
            putBoolean(ARG_IS_ACTIVE, isListOfActivePayments)
        }
    }

}
