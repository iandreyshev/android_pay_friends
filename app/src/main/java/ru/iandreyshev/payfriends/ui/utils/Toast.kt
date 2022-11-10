package ru.iandreyshev.payfriends.ui.utils

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.toast(message: CharSequence) =
    requireContext().toast(message)

fun Fragment.toast(@StringRes messageRes: Int) =
    toast(getString(messageRes))
