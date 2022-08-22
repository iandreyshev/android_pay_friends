package ru.iandreyshev.payfriends.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentRecyclerViewBinding
import ru.iandreyshev.payfriends.databinding.ItemPaymentResultBinding
import ru.iandreyshev.payfriends.domain.core.Transaction
import ru.iandreyshev.payfriends.presentation.payment.PaymentViewModel
import ru.iandreyshev.payfriends.presentation.payment.State
import ru.iandreyshev.payfriends.ui.utils.uiLazy
import ru.iandreyshev.payfriends.ui.utils.viewBindings

class PaymentResultFragment : Fragment(R.layout.fragment_recycler_view) {

    private val mViewModel by viewModels<PaymentViewModel>(
        ownerProducer = { requireParentFragment() }
    )
    private val mBinding by viewBindings(FragmentRecyclerViewBinding::bind)
    private val mIsResult by uiLazy { arguments?.getBoolean(ARG_KEY, false) ?: false }
    private val mAdapter by uiLazy { PaymentResultAdapter(mIsResult) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        mViewModel.state.observe(viewLifecycleOwner, ::render)
    }

    private fun initRecyclerView() {
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun render(state: State) {
        mAdapter.submitList(
            when {
                mIsResult -> state.result
                else -> state.history
            }
        )
    }

    companion object {
        private const val ARG_KEY = "is_result"

        fun newInstance(isResult: Boolean) = PaymentResultFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_KEY, isResult)
            }
        }
    }

}
