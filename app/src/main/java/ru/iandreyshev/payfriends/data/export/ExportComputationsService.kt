package ru.iandreyshev.payfriends.data.export

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.withContext
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.data.storage.json.JsonStorage
import ru.iandreyshev.payfriends.system.Dispatchers
import ru.iandreyshev.payfriends.ui.utils.toast
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ExportComputationsService
@Inject constructor(
    private val context: Context,
    private val storage: JsonStorage,
    private val dispatchers: Dispatchers
) {

    suspend operator fun invoke() {
        withContext(dispatchers.io) {
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val resultFile = File(downloadsFolder, RESULT_FILE_NAME)

            if (!resultFile.exists()) {
                resultFile.setWritable(true)
                resultFile.setReadable(true)
                resultFile.createNewFile()
            }

            try {
                storage.memoryFile.copyTo(resultFile, overwrite = true)
                withContext(dispatchers.ui) {
                    context.toast(context.getString(R.string.export_result_success, RESULT_FILE_NAME))
                }
            } catch (ex: Exception) {
                Timber.d(ex)
            }
        }
    }

    companion object {
        private const val RESULT_FILE_NAME = "computations.json"
    }

}
