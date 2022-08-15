package ru.iandreyshev.stale.presentation.transactionEditor

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.iandreyshev.stale.data.payment.InMemoryPaymentsStorage
import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.TransactionId
import ru.iandreyshev.stale.domain.transactionEditor.FilterMembers
import ru.iandreyshev.stale.system.AppDispatchers
import ru.iandreyshev.stale.ui.utils.uiLazy

class TransactionEditorViewModel(
    paymentId: PaymentId,
    transactionId: TransactionId?
) : ViewModel() {

    val state by uiLazy { mStore.states }
    val labels by uiLazy { mStore.labels }

    private val mStore = DefaultStoreFactory()
        .create(
            name = "Transaction editor store",
            initialState = State.default(),
            executorFactory = {
                Executor(
                    dispatchers = AppDispatchers,
                    storage = InMemoryPaymentsStorage(),
                    filterMembers = FilterMembers()
                )
            },
            reducer = Reducer,
            bootstrapper = object : CoroutineBootstrapper<Action>() {
                override fun invoke() =
                    dispatch(Action.OnStart(paymentId, transactionId))
            }
        )

    fun onIntent(intent: Intent) = mStore.accept(intent)

}
