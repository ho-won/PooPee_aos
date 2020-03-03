package kr.co.ho1.poopee.common.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_toolbar.view.*
import kr.co.ho1.poopee.R

class ToolbarView : RelativeLayout {
    private lateinit var onBtnLeftOne: (() -> Unit)
    private lateinit var onBtnLeftTwo: (() -> Unit)
    private lateinit var onBtnRightTwo: (() -> Unit)
    private lateinit var onBtnRightOne: (() -> Unit)

    constructor(context: Context) : super(context, null) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_toolbar, this, true)
        setListener()
    }

    private fun setListener() {
        btn_left_one.setOnClickListener { onBtnLeftOne() }
        btn_left_two.setOnClickListener { onBtnLeftTwo() }
        btn_right_two.setOnClickListener { onBtnRightTwo() }
        btn_right_one.setOnClickListener { onBtnRightOne() }
    }

    fun setSelectedListener(onBtnLeftOne: (() -> Unit), onBtnLeftTwo: (() -> Unit), onBtnRightTwo: (() -> Unit), onBtnRightOne: (() -> Unit)) {
        this.onBtnLeftOne = onBtnLeftOne
        this.onBtnLeftTwo = onBtnLeftTwo
        this.onBtnRightTwo = onBtnRightTwo
        this.onBtnRightOne = onBtnRightOne
    }

    fun setTitle(title: String) {
        tv_toolbar_title.visibility = View.VISIBLE
        tv_toolbar_title.text = title
    }

    fun setImageLeftOne(drawable: Drawable?) {
        if (drawable == null) {
            btn_left_one.visibility = View.INVISIBLE
        } else {
            btn_left_one.visibility = View.VISIBLE
            btn_left_one.setImageDrawable(drawable)
        }
    }

    fun setImageLeftTwo(drawable: Drawable?) {
        if (drawable == null) {
            btn_left_two.visibility = View.INVISIBLE
        } else {
            btn_left_two.visibility = View.VISIBLE
            btn_left_two.setImageDrawable(drawable)
        }
    }

    fun setImageRightTwo(drawable: Drawable?) {
        if (drawable == null) {
            btn_right_two.visibility = View.INVISIBLE
        } else {
            btn_right_two.visibility = View.VISIBLE
            btn_right_two.setImageDrawable(drawable)
        }
    }

    fun setImageRightOne(drawable: Drawable?) {
        if (drawable == null) {
            btn_right_one.visibility = View.INVISIBLE
        } else {
            btn_right_one.visibility = View.VISIBLE
            btn_right_one.setImageDrawable(drawable)
        }
    }

    override fun getRootView(): RelativeLayout? {
        return root_view
    }

}