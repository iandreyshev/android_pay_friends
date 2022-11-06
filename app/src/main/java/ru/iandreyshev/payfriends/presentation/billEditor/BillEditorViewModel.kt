package ru.iandreyshev.payfriends.presentation.billEditor

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.iandreyshev.payfriends.domain.billEditor.FilterMembersUseCase
import ru.iandreyshev.payfriends.domain.billEditor.SaveBillUseCase
import ru.iandreyshev.payfriends.domain.computationEditor.ValidateMemberUseCase
import ru.iandreyshev.payfriends.domain.computationsList.Storage
import ru.iandreyshev.payfriends.domain.core.BillId
import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.system.Dispatchers
import ru.iandreyshev.payfriends.ui.utils.uiLazy
import javax.inject.Inject

class BillEditorViewModel
@Inject constructor(
    private val storage: Storage,
    private val dispatchers: Dispatchers,
    private val filterMembers: FilterMembersUseCase,
    private val saveBills: SaveBillUseCase,
    private val validateMembers: ValidateMemberUseCase,
    private val resources: Resources
) : ViewModel() {

    val state by uiLazy { mStore.states }
    val labels by uiLazy { mStore.labels }

    private lateinit var mStore: Store<Intent, State, Label>

    fun onViewCreated(computationId: ComputationId, billId: BillId?) {
        mStore = DefaultStoreFactory()
            .create(
                name = "Bill editor store",
                initialState = State.default().copy(
                    computationId = computationId,
                    billId = billId
                ),
                executorFactory = {
                    Executor(
                        dispatchers = dispatchers,
                        storage = storage,
                        filterMembers = filterMembers,
                        validateMember = validateMembers,
                        saveBill = saveBills,
                        getDefaultTitle = DefaultBillTitleProvider(resources)
                    )
                },
                reducer = Reducer,
                bootstrapper = object : CoroutineBootstrapper<Action>() {
                    override fun invoke() =
                        dispatch(Action.OnStart(computationId, billId))
                }
            )
    }

    operator fun invoke(intent: Intent) = mStore.accept(intent)

}
