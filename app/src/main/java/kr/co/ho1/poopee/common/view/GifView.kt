package kr.co.ho1.poopee.common.view

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
    private var inputStream: InputStream? = null
    private var movie: Movie? = null
    private var width = 0
    private var height = 0
    private var start: Long = 0
    private var context: Context? = null

    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.context = context
    }

    private fun init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        isFocusable = true
        movie = Movie.decodeStream(inputStream)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        if (movie != null) {
            canvas.scale(width.toFloat() / movie!!.width().toFloat(), height.toFloat() / movie!!.height().toFloat())
            val now = SystemClock.uptimeMillis()

            if (start == 0L) {
                start = now
            }

            var duration = movie!!.duration()
            if (duration == 0) {
                duration = 1000
            }

            val relTime = ((now - start) % duration).toInt()

            movie!!.setTime(relTime)
            movie!!.draw(canvas, 0f, 0f)
            invalidate()
        }
    }

    fun setGifImageResource(id: Int, width: Int, height: Int) {
        inputStream = context!!.resources.openRawResource(id)
        this.width = width
        this.height = height
        init()
    }

    fun setGifImageUri(uri: Uri) {
        try {
            inputStream = context!!.contentResolver.openInputStream(uri)
            init()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    fun stopGif() {
        movie = null
    }

}
