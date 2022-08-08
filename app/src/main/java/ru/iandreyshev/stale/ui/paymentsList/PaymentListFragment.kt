package ru.iandreyshev.stale.ui.paymentsList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.presentation.paymentsList.PaymentsListViewModel
import timber.log.Timber

class PaymentListFragment : Fragment(R.layout.fragment_payments_list) {

    private val mViewModel by viewModels<PaymentsListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d(mViewModel.toString())
    }

}
