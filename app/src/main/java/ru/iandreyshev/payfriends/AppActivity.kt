package ru.iandreyshev.payfriends

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.iandreyshev.payfriends.databinding.ActivityMainBinding
import ru.iandreyshev.payfriends.ui.paymentsList.PaymentListFragment
import ru.iandreyshev.payfriends.ui.utils.uiLazy
import ru.iandreyshev.payfriends.ui.utils.viewBindings

class AppActivity : AppCompatActivity() {

    private val mBinding by viewBindings(ActivityMainBinding::bind)
    private val mNavController by uiLazy {
        (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment)
            .navController
    }
    private val mDestinationsWithMenu = setOf(
        R.id.activePaymentsListDest,
        R.id.completedPaymentsListDest,
        R.id.settingsDest
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMenu()
    }

    private fun initMenu() {
        mBinding.menuView.setupWithNavController(mNavController)
        mBinding.menuView.setOnItemSelectedListener { item ->
            val args = when (item.itemId) {
                R.id.activePaymentsListDest -> PaymentListFragment.args(isListOfActivePayments = true)
                R.id.completedPaymentsListDest -> PaymentListFragment.args(isListOfActivePayments = false)
                else -> bundleOf()
            }
            mNavController.navigate(item.itemId, args)
            true
        }
        mNavController.addOnDestinationChangedListener { _, destination, _ ->
            mBinding.menuView.isVisible = destination.id in mDestinationsWithMenu
        }
    }

}
