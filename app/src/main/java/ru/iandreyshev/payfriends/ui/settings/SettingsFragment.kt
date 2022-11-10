package ru.iandreyshev.payfriends.ui.settings

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.data.settings.AppSettings
import ru.iandreyshev.payfriends.databinding.FragmentSettingsBinding
import ru.iandreyshev.payfriends.presentation.settings.SettingsViewModel
import ru.iandreyshev.payfriends.ui.utils.*
import timber.log.Timber


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBindings(FragmentSettingsBinding::bind)
    private val viewModel by viewModelsDiFactory<SettingsViewModel>()
    private val settings by uiLazy { AppSettings(requireContext()) }
    private val selectFileLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        onFinishImport(result)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.mipmap.ic_launcher))
        binding.version.text = requireContext().getAppVersionInfo()

        binding.calcModeSettings.setOnClickListener { }
        binding.calcModeSwitch.isChecked = settings.get().isSmartAlgorithm
        binding.calcModeSwitch.setOnCheckedChangeListener { _, b ->
            settings.save(settings.get().copy(isSmartAlgorithm = b))
        }

        binding.exportMemoryButton.setOnClickListener { exportFile() }

        binding.importMemoryButton.setOnClickListener { importFile() }
    }

    private fun importFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = MimeTypeMap.getSingleton().getMimeTypeFromExtension("json")
        selectFileLauncher.launch(intent)
    }

    private fun exportFile() {
        viewModel.onExport()
    }

    private fun onFinishImport(result: ActivityResult) {
        when (val uri = result.data?.data) {
            null -> toast(R.string.common_unknown_error)
            else -> {
                requireContext().contentResolver
                    .openInputStream(uri)
                    ?.use {  stream ->
                        val text = stream.reader().readText()
                        viewModel.onImport(text)
                    }
                    ?: kotlin.run {
                        toast(R.string.common_unknown_error)
                    }
            }
        }
    }

}
