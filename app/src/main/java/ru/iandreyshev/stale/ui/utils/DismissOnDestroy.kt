package ru.iandreyshev.stale.ui.utils

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu

fun PopupMenu?.dismissOnDestroy() {
    this ?: return
    setOnDismissListener(null)
    dismiss()
}

fun AlertDialog?.dismissOnDestroy() {
    this ?: return
    setOnDismissListener(null)
    dismiss()
}
