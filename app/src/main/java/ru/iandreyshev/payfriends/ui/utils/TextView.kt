package ru.iandreyshev.payfriends.ui.utils

import android.widget.TextView

fun TextView.setTextIfChanged(text: String) {
    if (this.text.toString() != text) {
        setText(text)
    }
}
