package ru.iandreyshev.stale.ui.utils

import androidx.appcompat.widget.PopupMenu

fun PopupMenu?.dismissOnDestroy() {
    this ?: return
    setOnDismissListener(null)
    dismiss()
}
