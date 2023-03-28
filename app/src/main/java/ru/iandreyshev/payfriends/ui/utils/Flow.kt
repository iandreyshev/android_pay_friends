package ru.iandreyshev.payfriends.ui.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun<T> Flow<T>.onEach(lifecycle: Lifecycle, action: (T) -> Unit) {
    flowWithLifecycle(lifecycle)
        .onEach(action)
        .launchIn(lifecycle.coroutineScope)
}
