package ru.iandreyshev.stale.presentation.transactionEditor

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.iandreyshev.stale.ui.utils.uiLazy

class TransactionEditorViewModel : ViewModel() {

    val state by uiLazy { mStore.states }
    val labels by uiLazy { mStore.labels }

    private val mStore = DefaultStoreFactory()
        .create(
            name = "Transaction editor store",
            initialState = State.default(),
            executorFactory = {
                Executor()
            },
            reducer = Reducer
        )

    fun onIntent(intent: Intent) = mStore.accept(intent)

}
