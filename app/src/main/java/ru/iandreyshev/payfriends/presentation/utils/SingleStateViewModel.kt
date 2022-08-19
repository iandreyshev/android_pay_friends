package ru.iandreyshev.payfriends.presentation.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.iandreyshev.payfriends.ui.utils.uiLazy

open class SingleStateViewModel<TState : Any, TEvent>(
    initialState: TState
) : ViewModel() {

    val state: LiveData<TState> by uiLazy { mState }
    val event: LiveData<TEvent> by uiLazy { mEvent }

    private val mState = MutableLiveData(initialState)
    private val mEvent = SingleLiveEvent<TEvent>()

    protected fun modifyState(state: TState) = modifyState { state }

    protected fun modifyState(modifier: TState.() -> TState) {
        mState.value = mState.value?.let(modifier)
    }

    protected fun event(eventBuilder: () -> TEvent) {
        mEvent.value = eventBuilder()
    }

    protected fun event(event: TEvent) = event { event }

    protected fun getState(): TState = state.value!!

}
