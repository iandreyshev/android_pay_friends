package ru.iandreyshev.payfriends.ui.payment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import ru.iandreyshev.payfriends.App
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentPaymentBinding
import ru.iandreyshev.payfriends.domain.calc.CalcResultUseCase
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.payment.GetPaymentUseCase
import ru.iandreyshev.payfriends.presentation.payment.Event
import ru.iandreyshev.payfriends.presentation.payment.PaymentViewModel
import ru.iandreyshev.payfriends.presentation.payment.State
import ru.iandreyshev.payfriends.system.AppDispatchers
import ru.iandreyshev.payfriends.ui.utils.uiLazy
import ru.iandreyshev.payfriends.ui.utils.viewBindings
import ru.iandreyshev.payfriends.ui.utils.viewModelFactory

class PaymentFragment : Fragment(R.layout.fragment_payment) {

    private val mViewModel by viewModelFactory {
        PaymentViewModel(
            id = PaymentId(mArgs.paymentId),
            name = mArgs.paymentName,
            calcResult = CalcResultUseCase(AppDispatchers),
            getPayment = GetPaymentUseCase(
                storage = App.storage,
                dispatchers = AppDispatchers
            )
        )
    }
    private val mBinding by viewBindings(FragmentPaymentBinding::bind)
    private val mArgs by navArgs<PaymentFragmentArgs>()
    private val mNavController by uiLazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAppBar()
        initViewPager()
        initAddTransactionButton()

        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mViewModel.event.observe(viewLifecycleOwner, ::handleEvent)
    }

    private fun initViewPager() {
        mViewModel.toString() // To initialize Viewmodel before child fragment
        mBinding.viewPager.adapter = PaymentViewPagerAdapter(this)
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.payment_tab_result_title)
                else -> getString(R.string.payment_tab_history_title)
            }
        }.attach()
    }

    private fun initAppBar() {
        mBinding.toolbar.setNavigationOnClickListener { mViewModel.onExit() }
    }

    private fun initAddTransactionButton() {
        mBinding.emptyViewButton.setOnClickListener { mViewModel.onAddTransaction() }
        mBinding.addPaymentButton.setOnClickListener { mViewModel.onAddTransaction() }
    }

    private fun render(state: State) {
        mBinding.toolbar.title = state.name

        mBinding.tabLayout.isVisible = state.history.isNotEmpty()
        mBinding.viewPager.isVisible = state.history.isNotEmpty()

        mBinding.emptyViewGroup.isVisible = state.history.isEmpty()

        mBinding.addPaymentButton.isVisible = state.history.isNotEmpty()
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.Exit -> mNavController.popBackStack()
            is Event.OpenTransactionEditor ->
                PaymentFragmentDirections
                    .actionAddTransactionsToPayment(event.id.value, null)
                    .let(mNavController::navigate)
        }
    }

}
