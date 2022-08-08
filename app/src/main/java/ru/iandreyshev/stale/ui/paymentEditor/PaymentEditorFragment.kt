package ru.iandreyshev.stale.ui.paymentEditor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.FragmentStubBinding
import ru.iandreyshev.stale.ui.utils.viewBindings

class PaymentEditorFragment : Fragment(R.layout.fragment_stub) {

    private val mBinding by viewBindings(FragmentStubBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.textView.text = "PaymentEditor"
    }

}
