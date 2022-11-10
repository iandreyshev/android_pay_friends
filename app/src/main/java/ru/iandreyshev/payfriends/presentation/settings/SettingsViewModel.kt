package ru.iandreyshev.payfriends.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iandreyshev.payfriends.data.export.ExportComputationsService
import ru.iandreyshev.payfriends.domain.settings.ImportComputationsUseCase
import ru.iandreyshev.payfriends.domain.settings.JsonComputations
import javax.inject.Inject

class SettingsViewModel
@Inject constructor(
    private val importComputations: ImportComputationsUseCase,
    private val exportComputations: ExportComputationsService
) : ViewModel() {

    fun onImport(text: String) {
        viewModelScope.launch {
            importComputations(JsonComputations(text))
        }
    }

    fun onExport() {
        viewModelScope.launch {
            exportComputations()
        }
    }

}
