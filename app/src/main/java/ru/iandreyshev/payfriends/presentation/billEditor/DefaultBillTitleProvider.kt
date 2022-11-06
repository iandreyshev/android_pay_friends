package ru.iandreyshev.payfriends.presentation.billEditor

import android.content.res.Resources
import ru.iandreyshev.payfriends.R

class DefaultBillTitleProvider(
    private val resources: Resources
) {

    operator fun invoke(number: Int): String =
        resources.getString(R.string.bill_editor_bill_default_title, number)

}
