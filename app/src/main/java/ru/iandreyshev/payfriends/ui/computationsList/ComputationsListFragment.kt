package ru.iandreyshev.payfriends.ui.computationsList

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentComputationsListBinding
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.presentation.computationsList.ComputationsListViewModel
import ru.iandreyshev.payfriends.presentation.computationsList.Event
import ru.iandreyshev.payfriends.presentation.computationsList.State
import ru.iandreyshev.payfriends.ui.utils.dismissOnDestroy
import ru.iandreyshev.payfriends.ui.utils.uiLazy
import ru.iandreyshev.payfriends.ui.utils.viewBindings
import ru.iandreyshev.payfriends.ui.utils.viewModelsDiFactory

class ComputationsListFragment : Fragment(R.layout.fragment_computations_list) {

    private val mBinding by viewBindings(FragmentComputationsListBinding::bind)
    private val mViewModel by viewModelsDiFactory<ComputationsListViewModel>()
    private val isCompleted by uiLazy { arguments?.getBoolean(ARG_IS_COMPLETED, true) ?: true }
    private val mAdapter by uiLazy {
        ComputationsAdapter(
            isCompleted = isCompleted,
            onClick = mViewModel::onOpen,
            onAddBill = mViewModel::onAddBill,
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

        if (savedInstanceState == null) {
            mViewModel.onViewCreated(isCompleted)
        }
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
            mViewModel.onNewComputation()
        }
        mBinding.emptyViewButton.setOnClickListener {
            mViewModel.onNewComputation()
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
        mBinding.emptyViewGroup.isVisible = state.computations.isEmpty()
        mBinding.emptyViewButton.isVisible = state.computations.isEmpty() && state.isCompleted
        mBinding.listView.isVisible = state.computations.isNotEmpty()
        mBinding.newPaymentButton.isVisible = state.computations.isNotEmpty() && state.isCompleted
        mAdapter.submitList(state.computations)

        mBinding.toolbarTitle.text = when (state.isCompleted) {
            true -> getString(R.string.menu_item_active_payments_title)
            else -> getString(R.string.menu_item_completed_payments_title)
        }

        mBinding.emptyViewText.text = when (state.isCompleted) {
            true -> getString(R.string.computations_list_empty_view_text_active)
            else -> getString(R.string.common_empty_list)
        }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.NavigateToPayment ->
                ComputationsListFragmentDirections
                    .actionOpenPayment(event.id.value, event.name)
                    .let(mNavController::navigate)
            is Event.NavigateToComputationEditor ->
                ComputationsListFragmentDirections
                    .actionEditPayment(event.id?.value)
                    .let(mNavController::navigate)
            is Event.NavigateToBillEditor ->
                ComputationsListFragmentDirections
                    .actionAddBill(event.computationId.value, null)
                    .let(mNavController::navigate)
            is Event.ShowPaymentDeletingDialog -> TODO()
        }
    }

    private fun onOptionsMenuOpen(view: View, id: ComputationId) {
        mPopupMenu = PopupMenu(requireContext(), view).apply {
            inflate(
                when (isCompleted) {
                    true -> R.menu.menu_active_payment_list_options
                    else -> R.menu.menu_completed_payment_list_options
                }
            )
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.paymentOptionComplete -> mViewModel.onCompletePayment(id, isCompleted = true)
                    R.id.paymentOptionMakeNotCompleted -> mViewModel.onCompletePayment(id, isCompleted = false)
                    R.id.paymentOptionEdit -> mViewModel.onEditComputation(id)
                    R.id.paymentOptionDelete -> mViewModel.onDeletePayment(id)
                }
                return@setOnMenuItemClickListener true
            }
            show()
        }
    }

    companion object {
        private const val ARG_IS_COMPLETED = "is_completed"

        fun args(isListOfActivePayments: Boolean) = Bundle().apply {
            putBoolean(ARG_IS_COMPLETED, isListOfActivePayments)
        }
    }

}
