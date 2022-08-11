package ru.iandreyshev.stale.ui.transactionEditor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.presentation.transactionEditor.TransactionEditorViewModel
import ru.iandreyshev.stale.ui.utils.uiLazy
import ru.iandreyshev.stale.ui.utils.viewModelFactory

class TransactionEditorFragment : Fragment(R.layout.fragment_transaction_editor) {

    private val mViewModel by viewModelFactory {
        TransactionEditorViewModel()
    }
    private val mArgs by navArgs<TransactionEditorFragmentArgs>()
    private val mAdapter by uiLazy {  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
    }

    private fun initRecyclerView() {

    }

}
