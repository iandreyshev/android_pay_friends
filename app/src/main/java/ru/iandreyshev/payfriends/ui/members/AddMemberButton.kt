package ru.iandreyshev.payfriends.ui.members

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ru.iandreyshev.payfriends.databinding.ViewAddMemberButtonBinding
import java.util.*

class AddMemberButton
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private lateinit var mBinding: ViewAddMemberButtonBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        mBinding = ViewAddMemberButtonBinding
            .inflate(LayoutInflater.from(context), this, true)
    }

    fun setState(name: String, onClickListener: () -> Unit = {}) {
        mBinding.memberCandidate.text = name
        mBinding.clickableArea.setOnClickListener {
            onClickListener()
        }
    }

}

