package ru.iandreyshev.payfriends.ui.computation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentRecyclerViewBinding
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
    private val mAdapter by uiLazy { ComputationResultAdapter(mIsResult) }

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

        fun newInstance(isResult: Boolean) = ComputationResultFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_KEY, isResult)
            }
        }
    }

}
