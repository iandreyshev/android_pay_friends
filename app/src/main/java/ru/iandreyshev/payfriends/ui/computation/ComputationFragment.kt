package ru.iandreyshev.payfriends.ui.computation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentComputationBinding
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.presentation.computation.ComputationViewModel
import ru.iandreyshev.payfriends.presentation.computation.Event
import ru.iandreyshev.payfriends.presentation.computation.State
import ru.iandreyshev.payfriends.ui.utils.uiLazy
import ru.iandreyshev.payfriends.ui.utils.viewBindings
import ru.iandreyshev.payfriends.ui.utils.viewModelsDiFactory

class ComputationFragment : Fragment(R.layout.fragment_computation) {

    private val mViewModel by viewModelsDiFactory<ComputationViewModel>()
    private val mBinding by viewBindings(FragmentComputationBinding::bind)
    private val mArgs by navArgs<ComputationFragmentArgs>()
    private val mNavController by uiLazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAppBar()
        initViewPager()
        initAddBillButton()

        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mViewModel.event.observe(viewLifecycleOwner, ::handleEvent)

        if (savedInstanceState == null) {
            mViewModel.onViewCreated(id = ComputationId(mArgs.id), name = mArgs.name)
        }
    }

    private fun initViewPager() {
        mViewModel.toString() // To initialize Viewmodel before child fragment
        mBinding.viewPager.adapter = ComputationViewPagerAdapter(this)
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.computation_tab_result_title)
                else -> getString(R.string.computation_tab_history_title)
            }
        }.attach()
    }

    private fun initAppBar() {
        mBinding.toolbar.setNavigationOnClickListener { mViewModel.onExit() }
    }

    private fun initAddBillButton() {
        mBinding.emptyViewButton.setOnClickListener { mViewModel.onAddBill() }
        mBinding.addPaymentButton.setOnClickListener { mViewModel.onAddBill() }
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
            is Event.OpenBillEditor ->
                ComputationFragmentDirections
                    .actionAddBillToPayment(event.id.value, null)
                    .let(mNavController::navigate)
        }
    }

}
