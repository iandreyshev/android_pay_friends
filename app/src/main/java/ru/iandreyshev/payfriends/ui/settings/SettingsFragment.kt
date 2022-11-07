package ru.iandreyshev.payfriends.ui.settings

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.data.settings.AppSettings
import ru.iandreyshev.payfriends.databinding.FragmentSettingsBinding
import ru.iandreyshev.payfriends.ui.utils.uiLazy
import ru.iandreyshev.payfriends.ui.utils.viewBindings

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBindings(FragmentSettingsBinding::bind)
    private val settings by uiLazy { AppSettings(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.mipmap.ic_launcher))
        binding.version.text = requireContext().getAppVersionInfo()

        binding.calcModeSwitch.isChecked = settings.get().isSmartAlgorithm
        binding.calcModeSwitch.setOnCheckedChangeListener { _, b ->
            settings.save(settings.get().copy(isSmartAlgorithm = b))
        }
    }

    private fun Context.getAppVersionInfo(): String {
        return with(packageInfo() ?: return "") {
            "$versionName ($versionCode)"
        }
    }

    private fun Context?.packageInfo(): PackageInfo? {
        return this?.packageManager?.getPackageInfo(this.packageName, 0)
    }


}
