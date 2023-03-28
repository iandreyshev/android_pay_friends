package ru.iandreyshev.payfriends.ui.utils

import android.widget.CompoundButton
import com.google.android.material.checkbox.MaterialCheckBox

fun MaterialCheckBox.setCheckedSilent(isChecked: Boolean, listener: CompoundButton.OnCheckedChangeListener) {
    setOnCheckedChangeListener(null)
    this.isChecked = isChecked
    setOnCheckedChangeListener(listener)
}
