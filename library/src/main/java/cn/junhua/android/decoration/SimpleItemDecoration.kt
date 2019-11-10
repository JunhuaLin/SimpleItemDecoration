package cn.junhua.android.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout

/**
 * 自定义分割线 ：<br/>
 * 可以设置分割线的padding<br/>
 * 可以设置跳过某些分割线<br/>
 * @author junhua.lin@jinfuzi.com<br></br>
 * CREATED 2018/7/4 13:47
 */
class SimpleItemDecoration(context: Context, orientation: Int) : RecyclerView.ItemDecoration() {

    interface SkipCallback {
        fun skip(viewPos: Int, itemCount: Int): Boolean
    }

    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL

        private const val TAG = "DividerItem"
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

    private var mDivider: Drawable? = null

    /**
     * Current orientation. Either [.HORIZONTAL] or [.VERTICAL].
     */
    private var mOrientation: Int = HORIZONTAL

    private val mBounds = Rect()

    private var mStartPadding = 0
    private var mEndPadding = 0

    private var isItemOffsets = true

    private var mSkipCallback: SkipCallback? = null


    init {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        if (mDivider == null) {
            Log.w(
                TAG,
                "@android:attr/listDivider was not set in the theme used for this " + "DividerItemDecoration. Please set that attribute all call setDrawable()"
            )
        }
        a.recycle()

        setOrientation(orientation)
    }

    /**
     * 是否需要绘制
     */
    fun setIsItemOffsets(isItemOffsets: Boolean): SimpleItemDecoration {
        this.isItemOffsets = isItemOffsets
        return this
    }

    /**
     * 设置跳过回调
     */
    fun setSkipCallback(skipCallback: SkipCallback?): SimpleItemDecoration {
        mSkipCallback = skipCallback
        return this
    }

    fun setSkipLast(): SimpleItemDecoration {
        mSkipCallback = object : SkipCallback {
            override fun skip(viewPos: Int, itemCount: Int): Boolean {
                return viewPos + 1 == itemCount
            }
        }
        return this
    }

    fun setSkipFirst(): SimpleItemDecoration {
        mSkipCallback = object : SkipCallback {
            override fun skip(viewPos: Int, itemCount: Int): Boolean {
                return viewPos == 0
            }
        }

        return this
    }

    fun setSkipFirstLast(): SimpleItemDecoration {
        mSkipCallback = object : SkipCallback {
            override fun skip(viewPos: Int, itemCount: Int): Boolean {
                return viewPos == 0 || viewPos == itemCount - 1
            }
        }

        return this
    }

    /**
     * 设置跳过多个分割线
     */
    fun setSkipList(vararg indexList: Int): SimpleItemDecoration {
        mSkipCallback = object : SkipCallback {
            override fun skip(viewPos: Int, itemCount: Int): Boolean {
                return viewPos in indexList
            }
        }
        return this
    }

    fun setPadding(padding: Int): SimpleItemDecoration {
        mStartPadding = padding
        mEndPadding = padding
        return this
    }

    fun setStartPadding(startPadding: Int): SimpleItemDecoration {
        mStartPadding = startPadding
        return this
    }

    fun setEndPadding(endPadding: Int): SimpleItemDecoration {
        mEndPadding = endPadding
        return this
    }

    /**
     * Sets the orientation for this divider. This should be called if
     * [RecyclerView.LayoutManager] changes orientation.
     *
     * @param orientation [.HORIZONTAL] or [.VERTICAL]
     */
    fun setOrientation(orientation: Int) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw IllegalArgumentException(
                "Invalid orientation. It should be either HORIZONTAL or VERTICAL"
            )
        }
        mOrientation = orientation
    }

    /**
     * Sets the [Drawable] for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    fun setDrawable(drawable: Drawable) {
        if (drawable == null) {
            throw IllegalArgumentException("Drawable cannot be null.")
        }
        mDivider = drawable
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (isItemOffsets) return

        if (parent.layoutManager == null || mDivider == null) {
            return
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (!isItemOffsets) return

        if (parent.layoutManager == null || mDivider == null) {
            return
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }

        val itemCount = getItemCount(parent)
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + Math.round(child.translationY)
            val top = bottom - mDivider!!.intrinsicHeight

            val viewPos = parent.getChildAdapterPosition(child)
            if (viewPos == RecyclerView.NO_POSITION) {
                continue
            }
            val isSkip = mSkipCallback?.skip(viewPos, itemCount) ?: false
            if (!isSkip) {
                mDivider!!.setBounds(left + mStartPadding, top, right - mEndPadding, bottom)
                mDivider!!.draw(canvas)
            }
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int

        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }

        val itemCount = getItemCount(parent)
        val childCount = parent.childCount
        val layoutManager = parent.layoutManager
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            layoutManager?.getDecoratedBoundsWithMargins(child, mBounds)
            val right = mBounds.right + Math.round(child.translationX)
            val left = right - mDivider!!.intrinsicWidth

            val viewPos = parent.getChildAdapterPosition(child)
            if (viewPos == RecyclerView.NO_POSITION) {
                continue
            }

            val isSkip = mSkipCallback?.skip(viewPos, itemCount) ?: false
            if (!isSkip) {
                mDivider!!.setBounds(left, top + mStartPadding, right, bottom - mEndPadding)
                mDivider!!.draw(canvas)
            }
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (!isItemOffsets) return

        val viewPos = parent.getChildAdapterPosition(view)
        if (viewPos == RecyclerView.NO_POSITION) {
            outRect.set(0, 0, 0, 0)
            return
        }

        val itemCount = getItemCount(parent)
        val isSkip = mSkipCallback?.skip(viewPos, itemCount) ?: false

        if (mDivider == null || isSkip) {
            outRect.set(0, 0, 0, 0)
            return
        }

        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
        } else {
            outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
        }
    }

    private fun getItemCount(parent: RecyclerView) =
        if (parent.adapter == null) 0 else parent.adapter!!.itemCount


}