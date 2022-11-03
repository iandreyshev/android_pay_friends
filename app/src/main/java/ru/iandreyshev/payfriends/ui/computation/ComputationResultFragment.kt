package ru.iandreyshev.payfriends.ui.computation

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentRecyclerViewBinding
import ru.iandreyshev.payfriends.domain.core.HistoryBill
import ru.iandreyshev.payfriends.presentation.computation.ComputationViewModel
import ru.iandreyshev.payfriends.presentation.computation.State
import ru.iandreyshev.payfriends.ui.utils.uiLazy
import ru.iandreyshev.payfriends.ui.utils.viewBindings

class ComputationResultFragment : Fragment(R.layout.fragment_recycler_view) {

    private val mViewModel by viewModels<ComputationViewModel>(
        ownerProducer = { requireParentFragment() }
    )
    private val mBinding by viewBindings(FragmentRecyclerViewBinding::bind)
    private val mIsResult by uiLazy { arguments?.getBoolean(ARG_KEY, false) ?: false }
    private val mResultAdapter by uiLazy { ComputationResultAdapter() }
    private val mHistoryAdapter by uiLazy { ComputationHistoryAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        mViewModel.state.observe(viewLifecycleOwner, ::render)
    }

    private fun initRecyclerView() {
        when {
            mIsResult -> {
                mBinding.recyclerView.adapter = mResultAdapter
                val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                decoration.setDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.gray_dark)))
                mBinding.recyclerView.addItemDecoration(decoration)
            }
            else -> {
                mBinding.recyclerView.adapter = mHistoryAdapter
            }
        }
    }

    private fun render(state: State) {
        mResultAdapter.submitList(state.result)
        mHistoryAdapter.submitList(state.history.flatMap { bill ->
            bill.transfers.mapIndexed { i, transfer ->
                ComputationHistoryAdapter.Item(
                    transfer = transfer,
                    isFirstInBill = i == 0,
                    isLastInBill = i == bill.transfers.lastIndex,
                    billDate = bill.date
                )
            }
        })
    }

    companion object {
        private const val ARG_KEY = "is_result"

        fun newInstance(isResult: Boolean) = ComputationResultFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_KEY, isResult)
            }
        }
    }

}
