package ru.iandreyshev.stale.ui.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.iandreyshev.stale.R
import ru.iandreyshev.stale.databinding.FragmentMenuBinding
import ru.iandreyshev.stale.presentation.paymentsList.PaymentsListViewModel
import ru.iandreyshev.stale.ui.utils.viewBindings

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private val mBinding by viewBindings(FragmentMenuBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
    }

    private fun initMenu() {
        val fragment = childFragmentManager.findFragmentById(R.id.navHostFragment) as? NavHostFragment
        val navController = fragment?.navController ?: return
        mBinding.menuView.setupWithNavController(navController)
    }

}
