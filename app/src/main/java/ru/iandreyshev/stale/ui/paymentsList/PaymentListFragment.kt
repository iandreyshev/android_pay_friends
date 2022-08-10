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
import ru.iandreyshev.stale.domain.payments.ArchivePaymentUseCase
import ru.iandreyshev.stale.domain.payments.GetPaymentsListUseCase
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
            storage = App.storage,
            archivePayment = ArchivePaymentUseCase(storage = App.storage),
            getList = GetPaymentsListUseCase(storage = App.storage)
        )
    }
    private val mAdapter by uiLazy {
        PaymentsAdapter(
            onClick = mViewModel::onOpenPayment,
            onAddTransaction = mViewModel::onAddTransaction,
            onOptionsMenuOpen = ::onOptionsMenuOpen
        )
    }
    private val mNavController by uiLazy { findNavController() }
    private var mPopupMenu: PopupMenu? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initNewPaymentButton()
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mViewModel.event.observe(viewLifecycleOwner, ::handleEvent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPopupMenu.dismissOnDestroy()
    }

    private fun initList() {
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
        mBinding.listView.isVisible = state.payments.isNotEmpty()
        mBinding.newPaymentButton.isVisible = state.payments.isNotEmpty()
        mAdapter.submitList(state.payments)
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.NavigateToPayment ->
                PaymentListFragmentDirections
                    .actionOpenPayment(event.id.value)
                    .let(mNavController::navigate)
            is Event.NavigateToPaymentEditor ->
                PaymentListFragmentDirections
                    .actionEditPayment(event.id?.value)
                    .let(mNavController::navigate)
            is Event.NavigateToTransactionEditor ->
                PaymentListFragmentDirections
                    .actionAddTransactions(event.id.value)
                    .let(mNavController::navigate)
            is Event.ShowPaymentDeletingDialog -> TODO()
        }
    }

    private fun onOptionsMenuOpen(view: View, id: PaymentId) {
        mPopupMenu = PopupMenu(requireContext(), view).apply {
            inflate(R.menu.menu_payment_options)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.paymentOptionComplete -> mViewModel.onArchivePayment(id, isArchive = true)
                    R.id.paymentOptionEdit -> mViewModel.onEditPayment(id)
                    R.id.paymentOptionDelete -> mViewModel.onDeletePayment(id)
                }
                return@setOnMenuItemClickListener true
            }
            show()
        }
    }

}
