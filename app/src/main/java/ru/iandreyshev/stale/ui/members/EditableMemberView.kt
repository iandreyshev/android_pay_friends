package ru.iandreyshev.stale.ui.members

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ru.iandreyshev.stale.databinding.ViewEditableMemberBinding
import java.util.*

class EditableMemberView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private lateinit var mBinding: ViewEditableMemberBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        mBinding = ViewEditableMemberBinding
            .inflate(LayoutInflater.from(context), this, true)
    }

    fun setState(
        name: String,
        onClickListener: () -> Unit = {},
        canRemove: Boolean = false,
        onRemoveListener: () -> Unit = {}
    ) {
        mBinding.firstChar.text = name.getFirstChar()
        mBinding.name.text = name
        mBinding.clickableArea.setOnClickListener {
            onClickListener()
        }
    }

    private fun String.getFirstChar(): String {
        return (firstOrNull() ?: "").toString()
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }

}
