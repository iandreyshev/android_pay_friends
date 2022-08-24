@file:Suppress("UNCHECKED_CAST")

package ru.iandreyshev.payfriends.ui.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.iandreyshev.payfriends.App

inline fun <reified T : ViewModel> Fragment.viewModelFactory(crossinline factory: () -> T) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return factory() as T
        }
    }
}

inline fun <reified T : ViewModel> Fragment.viewModelsDiFactory() = viewModels<T> {
    App.component.viewModelFactory
}
