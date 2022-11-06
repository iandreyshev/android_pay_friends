package ru.iandreyshev.payfriends.ui.computation

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentRecyclerViewBinding
import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.presentation.computation.ComputationViewModel
import ru.iandreyshev.payfriends.presentation.computation.State
import ru.iandreyshev.payfriends.ui.utils.dismissOnDestroy
import ru.iandreyshev.payfriends.ui.utils.uiLazy
import ru.iandreyshev.payfriends.ui.utils.viewBindings

class ComputationResultFragment : Fragment(R.layout.fragment_recycler_view) {

    private val mViewModel by viewModels<ComputationViewModel>(
        ownerProducer = { requireParentFragment() }
    )
    private val mBinding by viewBindings(FragmentRecyclerViewBinding::bind)
    private val mIsResult by uiLazy { arguments?.getBoolean(ARG_KEY, false) ?: false }
    private val mResultAdapter by uiLazy { ComputationResultAdapter() }
    private val mHistoryAdapter by uiLazy { ComputationHistoryAdapter(::onOptionsMenuOpen) }
    private var mPopupMenu: PopupMenu? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        mViewModel.state.observe(viewLifecycleOwner, ::render)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPopupMenu.dismissOnDestroy()
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
        mHistoryAdapter.submitList(state.history.flatMapIndexed { billIndex, bill ->
            bill.transfers.mapIndexed { transferIndex, transfer ->
                val isLastInBill = transferIndex == bill.transfers.lastIndex
                val isLastInLastBill = isLastInBill && billIndex == state.history.lastIndex
                ComputationHistoryAdapter.Item(
                    id = bill.id,
                    title = bill.title,
                    number = bill.number,
                    transfer = transfer,
                    isFirstInBill = transferIndex == 0,
                    isLastInBill = isLastInBill,
                    isLastInLastBill = isLastInLastBill,
                    billDate = bill.date
                )
            }
        })
    }

    private fun onOptionsMenuOpen(view: View, id: BillId) {
        mPopupMenu = PopupMenu(requireContext(), view).apply {
            inflate(R.menu.menu_computation_history_options)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.billHistoryOptionDelete -> mViewModel.onDeleteBill(id)
                    R.id.billHistoryOptionEdit -> mViewModel.onEditBill(id)
                }
                return@setOnMenuItemClickListener true
            }
            show()
        }
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
