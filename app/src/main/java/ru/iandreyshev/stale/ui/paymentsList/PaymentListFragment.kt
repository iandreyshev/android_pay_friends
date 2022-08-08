package ru.iandreyshev.stale.ui.paymentsList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.FragmentPaymentsListBinding
import ru.iandreyshev.stale.domain.payment.PaymentId
import ru.iandreyshev.stale.presentation.paymentsList.*
import ru.iandreyshev.stale.ui.utils.uiLazy
import ru.iandreyshev.stale.ui.utils.viewBindings

class PaymentListFragment : Fragment(R.layout.fragment_payments_list) {

    private val mBinding by viewBindings(FragmentPaymentsListBinding::bind)
    private val mViewModel by viewModels<PaymentsListViewModel>()
    private val mAdapter by uiLazy {
        PaymentsAdapter(
            onClick = mViewModel::onOpenPayment,
            onAddTransaction = mViewModel::onAddTransaction,
            onOptionsMenuOpen = ::onOptionsMenuOpen
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initNewPaymentButton()
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mViewModel.event.observe(viewLifecycleOwner, ::handleEvent)
    }

    private fun initList() {
        mBinding.listView.adapter = mAdapter
    }

    private fun initNewPaymentButton() {
        mBinding.newPaymentButton.setOnClickListener {
            mViewModel.onEditPayment(null)
        }
    }

    private fun render(state: State) {
        mAdapter.submitList(state.payments)
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is NavigateToPaymentEditor -> {
                val navController = findNavController()
                PaymentListFragmentDirections.actionPaymentListDestToNavPaymentEditor()
                    .let(navController::navigate)
            }
            is NavigateToTransactionEditor -> TODO()
            is ShowPaymentDeletingDialog -> TODO()
        }
    }

    private fun onOptionsMenuOpen(view: View, id: PaymentId) {
        // TODO: Show options menu
    }

}
