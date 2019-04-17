package kr.ho1.poopee.common.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.FrameLayout
import kr.ho1.poopee.R

class SquareFrameLayout : FrameLayout {
    companion object {
        const val WIDTH = 1
    }

    private var squareWidth: Int = 0
    private var squareHeight: Int = 0
    private var standard: Int = 0

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareFrameLayout, defStyleAttr, 0)
        squareWidth = a.getInt(R.styleable.SquareFrameLayout_square_width, 1)
        squareHeight = a.getInt(R.styleable.SquareFrameLayout_square_height, 1)
        standard = a.getInt(R.styleable.SquareFrameLayout_standard, WIDTH)
        a.recycle()
    }

    fun setSquareWidth(squareWidth: Int) {
        this.squareWidth = squareWidth
    }

    fun setSquareHeight(squareHeight: Int) {
        this.squareHeight = squareHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val oldHeight = MeasureSpec.getSize(heightMeasureSpec)
        val oldWidth = MeasureSpec.getSize(widthMeasureSpec)
        val newHeight: Int
        val newWidth: Int
        if (standard == WIDTH) {
            val square = squareHeight.toFloat() / squareWidth.toFloat()
            newHeight = (oldWidth * square).toInt()
            newWidth = oldWidth
        } else {
            val raito = squareWidth.toFloat() / squareHeight.toFloat()
            newHeight = oldHeight
            newWidth = (oldHeight / raito).toInt()
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY))
    }

}