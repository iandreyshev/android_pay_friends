package ru.iandreyshev.payfriends.presentation.billEditor

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.iandreyshev.payfriends.App
import ru.iandreyshev.payfriends.domain.billEditor.FilterMembers
import ru.iandreyshev.payfriends.domain.billEditor.SaveBillUseCase
import ru.iandreyshev.payfriends.domain.computationEditor.ValidateMemberUseCase
import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.system.AppDispatchers
import ru.iandreyshev.payfriends.ui.utils.uiLazy

class BillEditorViewModel(
    val computationId: ComputationId,
    val billId: BillId?
) : ViewModel() {

    val state by uiLazy { mStore.states }
    val labels by uiLazy { mStore.labels }

    private val mStore = DefaultStoreFactory()
        .create(
            name = "Bill editor store",
            initialState = State.default(),
            executorFactory = {
                Executor(
                    dispatchers = AppDispatchers,
                    storage = App.storage,
                    filterMembers = FilterMembers(),
                    validateMember = ValidateMemberUseCase(),
                    saveBill = SaveBillUseCase(
                        computationId = computationId,
                        dispatchers = AppDispatchers,
                        storage = App.storage,
                        dateProvider = App.dateProvider
                    )
                )
            },
            reducer = Reducer,
            bootstrapper = object : CoroutineBootstrapper<Action>() {
                override fun invoke() =
                    dispatch(Action.OnStart(computationId, billId))
            }
        )

    operator fun invoke(intent: Intent) = mStore.accept(intent)

}
