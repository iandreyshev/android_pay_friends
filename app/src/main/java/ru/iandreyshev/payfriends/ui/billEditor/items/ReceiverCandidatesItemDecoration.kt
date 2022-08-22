package ru.iandreyshev.payfriends.ui.billEditor.items

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.ui.utils.uiLazy

class ReceiverCandidatesItemDecoration(
    private val resources: Resources
) : RecyclerView.ItemDecoration() {

    private val mMargin by uiLazy { resources.getDimensionPixelSize(R.dimen.step_8) }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = mMargin
        outRect.bottom = mMargin
    }

}

