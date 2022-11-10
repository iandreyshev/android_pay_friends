package ru.iandreyshev.payfriends.ui.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.widget.Toast

fun Context.getAppVersionInfo(): String {
    return with(packageInfo() ?: return "") {
        "$versionName ($versionCode)"
    }
}

fun Context?.packageInfo(): PackageInfo? {
    return this?.packageManager?.getPackageInfo(this.packageName, 0)
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
