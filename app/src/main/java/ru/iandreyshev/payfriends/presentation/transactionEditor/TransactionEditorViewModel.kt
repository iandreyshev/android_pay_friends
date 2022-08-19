package ru.iandreyshev.payfriends.presentation.transactionEditor

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.iandreyshev.payfriends.App
import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.core.TransactionId
import ru.iandreyshev.payfriends.domain.paymentEditor.ValidateMemberUseCase
import ru.iandreyshev.payfriends.domain.transactionEditor.FilterMembers
import ru.iandreyshev.payfriends.domain.transactionEditor.SaveTransactionsUseCase
import ru.iandreyshev.payfriends.system.AppDispatchers
import ru.iandreyshev.payfriends.ui.utils.uiLazy

class TransactionEditorViewModel(
    val paymentId: PaymentId,
    val transactionId: TransactionId?
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
                    storage = App.storage,
                    filterMembers = FilterMembers(),
                    validateMember = ValidateMemberUseCase(),
                    saveTransactions = SaveTransactionsUseCase(
                        paymentId = paymentId,
                        dispatchers = AppDispatchers,
                        storage = App.storage,
                        dateProvider = App.dateProvider
                    )
                )
            },
            reducer = Reducer,
            bootstrapper = object : CoroutineBootstrapper<Action>() {
                override fun invoke() =
                    dispatch(Action.OnStart(paymentId, transactionId))
            }
        )

    operator fun invoke(intent: Intent) = mStore.accept(intent)

}
