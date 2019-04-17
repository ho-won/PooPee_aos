package kr.ho1.poopee.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Movie
import android.net.Uri
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import java.io.FileNotFoundException
import java.io.InputStream


class GifView : View {
    private var mInputStream: InputStream? = null
    private var mMovie: Movie? = null
    private var mWidth = 0
    private var mHeight = 0
    private var mStart: Long = 0
    private var mContext: Context? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
    }

    private fun init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        isFocusable = true
        mMovie = Movie.decodeStream(mInputStream)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas) {
        if (mMovie != null) {
            canvas.scale(mWidth.toFloat() / mMovie!!.width().toFloat(), mHeight.toFloat() / mMovie!!.height().toFloat())
            val now = SystemClock.uptimeMillis()

            if (mStart == 0L) {
                mStart = now
            }

            var duration = mMovie!!.duration()
            if (duration == 0) {
                duration = 1000
            }

            val relTime = ((now - mStart) % duration).toInt()

            mMovie!!.setTime(relTime)
            mMovie!!.draw(canvas, 0f, 0f)
            invalidate()
        }
    }

    fun setGifImageResource(id: Int, width: Int, height: Int) {
        mInputStream = mContext!!.resources.openRawResource(id)
        mWidth = width
        mHeight = height
        init()
    }

    fun setGifImageUri(uri: Uri) {
        try {
            mInputStream = mContext!!.contentResolver.openInputStream(uri)
            init()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    fun stopGif() {
        mMovie = null
    }

}
