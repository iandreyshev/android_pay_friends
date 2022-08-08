package ru.iandreyshev.stale

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.iandreyshev.stale.databinding.ActivityMainBinding
import ru.iandreyshev.stale.ui.utils.uiLazy
import ru.iandreyshev.stale.ui.utils.viewBindings
import timber.log.Timber

class AppActivity : AppCompatActivity() {

    private val mBinding by viewBindings(ActivityMainBinding::bind)
    private val mNavController by uiLazy {
        (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment)
            .navController
    }
    private val mDestinationsWithMenu = setOf(
        R.id.paymentListDest,
        R.id.archiveDest,
        R.id.settingsDest
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMenu()
    }

    private fun initMenu() {
        mBinding.menuView.setupWithNavController(mNavController)
        mNavController.addOnDestinationChangedListener { _, destination, _ ->
            mBinding.menuView.isVisible = destination.id in mDestinationsWithMenu
        }
    }

}
