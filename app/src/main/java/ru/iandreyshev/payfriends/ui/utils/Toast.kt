package ru.iandreyshev.payfriends.ui.utils

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.toast(message: CharSequence) =
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

fun Fragment.toast(@StringRes messageRes: Int) =
    toast(getString(messageRes))
