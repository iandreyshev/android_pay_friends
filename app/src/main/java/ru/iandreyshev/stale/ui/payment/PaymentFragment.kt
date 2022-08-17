package ru.iandreyshev.stale.ui.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import ru.iandreyshev.stale.App
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.FragmentPaymentBinding
import ru.iandreyshev.stale.domain.calc.CalcResultUseCase
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.payment.GetPaymentUseCase
import ru.iandreyshev.stale.presentation.payment.Event
import ru.iandreyshev.stale.presentation.payment.PaymentViewModel
import ru.iandreyshev.stale.presentation.payment.State
import ru.iandreyshev.stale.system.AppDispatchers
import ru.iandreyshev.stale.ui.utils.uiLazy
import ru.iandreyshev.stale.ui.utils.viewBindings
import ru.iandreyshev.stale.ui.utils.viewModelFactory

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
        mBinding.toolbar.setNavigationOnClickListener { mNavController.popBackStack() }
    }

    private fun render(state: State) {
        mBinding.toolbar.title = state.name
    }

    private fun handleEvent(event: Event) {
    }

}

private class PaymentViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = ITEMS_COUNT
    override fun createFragment(position: Int) = PaymentResultFragment.newInstance(isResult = position == 0)

    companion object {
        private const val ITEMS_COUNT = 2
    }
}
