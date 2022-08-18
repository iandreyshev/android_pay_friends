package ru.iandreyshev.stale.ui.utils

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService

fun View.showKeyboard() {
    getSystemService(context, InputMethodManager::class.java)?.let {
        requestFocus()
        it.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}
