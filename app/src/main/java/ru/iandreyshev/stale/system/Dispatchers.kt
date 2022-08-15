package ru.iandreyshev.stale.system

import kotlinx.coroutines.CoroutineDispatcher

interface Dispatchers {
    val io: CoroutineDispatcher
    val ui: CoroutineDispatcher
}

object AppDispatchers : Dispatchers {

    override val io: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.IO

    override val ui: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.Main

}
