package ru.iandreyshev.payfriends.ui.transactionEditor.items

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.payfriends.R
import ru.iandreyshev.payfriends.ui.utils.uiLazy

class ProducerCandidatesItemDecoration(
    private val resources: Resources
) : RecyclerView.ItemDecoration() {

    private val mMargin by uiLazy { resources.getDimensionPixelSize(R.dimen.step_16) }
    private val mGutter by uiLazy { resources.getDimensionPixelSize(R.dimen.step_8) }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.left = mMargin
        }

        val isLast = position == (parent.adapter?.itemCount ?: 0) - 1
        outRect.right = when {
            isLast -> mMargin
            else -> mGutter
        }
    }

}
