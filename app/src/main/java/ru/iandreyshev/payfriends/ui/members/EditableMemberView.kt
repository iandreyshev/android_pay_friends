package ru.iandreyshev.payfriends.ui.members

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.databinding.ViewEditableMemberBinding
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
        onRemoveListener: (() -> Unit)? = null
    ) {
        mBinding.firstChar.text = name.getFirstChar()
        mBinding.name.text = name
        mBinding.clickableArea.setOnClickListener {
            onClickListener()
        }

        if (onRemoveListener == null) {
            mBinding.name.updateLayoutParams<ConstraintLayout.LayoutParams> {
                marginEnd = resources.getDimensionPixelSize(R.dimen.step_10)
            }
            mBinding.removeButton.setOnClickListener(null)
            mBinding.removeButton.isVisible = false
        } else {
            mBinding.name.updateLayoutParams<ConstraintLayout.LayoutParams> {
                marginEnd = resources.getDimensionPixelSize(R.dimen.step_4)
            }
            mBinding.removeButton.setOnClickListener { onRemoveListener() }
            mBinding.removeButton.isVisible = true
        }
    }

    private fun String.getFirstChar(): String {
        return (firstOrNull() ?: "").toString()
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }

}
