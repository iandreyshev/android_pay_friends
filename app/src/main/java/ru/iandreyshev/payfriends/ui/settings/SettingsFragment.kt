package ru.iandreyshev.payfriends.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.FragmentStubBinding
import ru.iandreyshev.payfriends.ui.utils.viewBindings

class SettingsFragment : Fragment(R.layout.fragment_stub) {

    private val mBinding by viewBindings(FragmentStubBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.textView.text = "SettingsFragment"
    }

}
