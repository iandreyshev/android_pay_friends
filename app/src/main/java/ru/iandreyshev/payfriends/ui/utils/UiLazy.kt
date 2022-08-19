package ru.iandreyshev.payfriends.ui.utils

fun <T> uiLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
