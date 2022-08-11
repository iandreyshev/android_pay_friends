package ru.iandreyshev.stale.presentation.transactionEditor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor

class Executor: CoroutineExecutor<Intent, Any, State, Message, Label>()
