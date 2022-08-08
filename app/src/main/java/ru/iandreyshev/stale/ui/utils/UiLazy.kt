package ru.iandreyshev.stale.ui.utils

fun <T> uiLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
