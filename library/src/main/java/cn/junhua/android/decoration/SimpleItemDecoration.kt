package cn.junhua.android.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlin.math.roundToInt

/**
 * 自定义分割线 ：<br/>
 * 可以设置分割线的padding<br/>
 * 可以设置跳过某些分割线<br/>
 * @author junhualin<br></br>
 * CREATED 2018/7/4 13:47
 */
class SimpleItemDecoration private constructor(context: Context, orientation: Int) :
    RecyclerView.ItemDecoration() {
    //region callback
    /**
     * 分割线是否跳过绘制回调
     */
    interface SkipCallback {
        fun skip(viewPos: Int, itemCount: Int): Boolean
    }

    //endregion callback

    //region companion

    companion object {
        const val HORIZONTAL = LinearLayoutManager.HORIZONTAL
        const val VERTICAL = LinearLayoutManager.VERTICAL

        private const val TAG = "SimpleItemDecoration"
        private val ATTRS = intArrayOf(android.R.attr.listDivider)

        /**
         * 创建SimpleItemDecoration对象
         */
        fun create(context: Context, orientation: Int = VERTICAL): SimpleItemDecoration =
            SimpleItemDecoration(context, orientation)

    }
    //endregion filed

    //region filed

    private var mContext: Context

    private var mDivider: Drawable? = null

    /**
     * Current orientation. Either [.HORIZONTAL] or [.VERTICAL].
     */
    private var mOrientation: Int = HORIZONTAL

    /**
     * 设置分割线的高度
     */
    private var mHeight: Int

    private val mBounds = Rect()

    private var mStartPadding = 0
    private var mEndPadding = 0

    private var mEnableItemOffsets = true

    private var mSkipCallback: SkipCallback? = null

    //endregion filed

    //region init

    init {
        mContext = context
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()

        if (mDivider == null) {
            Log.w(
                TAG,
                "@android:attr/listDivider was not set in the theme used for this " + "DividerItemDecoration. Please set that attribute all call setDrawable()"
            )
        }

        mHeight = dip2px(0.5f)

        setOrientation(orientation)
    }
    //endregion init

    //region api

    /**
     * 设置分割线的高度,默认是dp值内部默认转化为px
     *
     * @param height 分割线的数值
     * @param isDp true为dp值，false为px值
     */
    fun setHeight(height: Float, isDp: Boolean = true): SimpleItemDecoration {
        if (height < 0) {
            throw java.lang.IllegalArgumentException("分割线的高度不能小于零")
        }

        this.mHeight = if (isDp) dip2px(height) else height.toInt()
        return this
    }

    /**
     * 是否通过移动item间距产生分割线<br/>
     *
     * @param enableItemOffsets true:不会再Item布局上绘制分割线，false：分割线会覆盖在Item布局上
     */
    fun enableItemOffsets(enableItemOffsets: Boolean): SimpleItemDecoration {
        this.mEnableItemOffsets = enableItemOffsets
        return this
    }

    /**
     * 设置跳过回调
     * @param {@link SkipCallback}
     */
    fun setSkipCallback(skipCallback: SkipCallback?): SimpleItemDecoration {
        mSkipCallback = skipCallback
        return this
    }

    /**
     * 设置跳过最后一个分割线回调
     */
    fun setSkipLast(): SimpleItemDecoration {
        mSkipCallback = object : SkipCallback {
            override fun skip(viewPos: Int, itemCount: Int): Boolean {
                return viewPos + 1 == itemCount
            }
        }
        return this
    }

    /**
     * 设置跳第一个分割线回调
     */
    fun setSkipFirst(): SimpleItemDecoration {
        mSkipCallback = object : SkipCallback {
            override fun skip(viewPos: Int, itemCount: Int): Boolean {
                return viewPos == 0
            }
        }

        return this
    }

    /**
     * 设置不绘制第一个和最后一个Item分割线
     */
    fun setSkipFirstLast(): SimpleItemDecoration {
        mSkipCallback = object : SkipCallback {
            override fun skip(viewPos: Int, itemCount: Int): Boolean {
                return viewPos == 0 || viewPos == itemCount - 1
            }
        }

        return this
    }

    /**
     * 设置跳过某些个分割线<br/>
     *
     * @param indexList 可变参数列表，需要跳过分割线绘制的Item索引
     */
    fun setSkipList(vararg indexList: Int): SimpleItemDecoration {
        mSkipCallback = object : SkipCallback {
            override fun skip(viewPos: Int, itemCount: Int): Boolean {
                return viewPos in indexList
            }
        }
        return this
    }

    /**
     * 同时设置分割线距离开始和末端的距离<br/>
     * @param padding 分割线距离开始和末端的距离
     */
    fun setPadding(padding: Int): SimpleItemDecoration {
        mStartPadding = padding
        mEndPadding = padding
        return this
    }

    /**
     * 设置分割线距离开始的距离<br/>
     *
     * @param startPadding 分割线距离开始的距离
     */
    fun setStartPadding(startPadding: Int): SimpleItemDecoration {
        mStartPadding = startPadding
        return this
    }

    /**
     * 设置分割线距离末端的距离
     * @param endPadding 分割线距离末端的距离
     */
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
    fun setDrawable(drawable: Drawable): SimpleItemDecoration {
        if (drawable == null) {
            throw IllegalArgumentException("Drawable cannot be null.")
        }
        mDivider = drawable
        return this
    }

    /**
     * Sets the [Drawable] for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    fun setDrawable(@DrawableRes drawableResId: Int): SimpleItemDecoration {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mDivider = mContext.resources.getDrawable(drawableResId, mContext.theme)
        } else {
            mDivider = mContext.resources.getDrawable(drawableResId)
        }

        return this
    }

    //endregion api

    //region private

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mEnableItemOffsets) return

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
        if (!mEnableItemOffsets) return

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

            val viewPos = parent.getChildAdapterPosition(child)
            if (viewPos == RecyclerView.NO_POSITION) {
                continue
            }

            val isSkip = mSkipCallback?.skip(viewPos, itemCount) ?: false
            if (!isSkip && mDivider != null) {
                val bottom = mBounds.bottom + child.translationY.roundToInt()
                var top: Int
                if (mHeight > 0) {
                    top = bottom - mHeight
                } else {
                    top = bottom - (mDivider?.intrinsicHeight ?: 0)
                }

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
            val right = mBounds.right + child.translationX.roundToInt()
            val left = right - (mDivider?.intrinsicWidth ?: 0)

            val viewPos = parent.getChildAdapterPosition(child)
            if (viewPos == RecyclerView.NO_POSITION) {
                continue
            }

            val isSkip = mSkipCallback?.skip(viewPos, itemCount) ?: false
            if (!isSkip) {
                mDivider?.setBounds(left, top + mStartPadding, right, bottom - mEndPadding)
                mDivider?.draw(canvas)
            }
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (!mEnableItemOffsets) return

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

        var intrinsicHeight = mDivider?.intrinsicHeight ?: 0
        intrinsicHeight = mHeight
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, intrinsicHeight)
        } else {
            outRect.set(0, 0, intrinsicHeight, 0)
        }
    }

    private fun getItemCount(parent: RecyclerView) = parent.adapter?.itemCount ?: 0

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private fun dip2px(dpValue: Float): Int {
        val scale = mContext.resources.displayMetrics.density;
        return (dpValue * scale + 0.5f).toInt();
    }

    //endregion private
}