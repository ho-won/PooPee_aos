package kr.co.ho1.poopee.common.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.databinding.ActivityCopyBinding
import kr.co.ho1.poopee.databinding.ViewToolbarBinding

class ToolbarView : RelativeLayout {
    public lateinit var binding: ViewToolbarBinding

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
        binding = ViewToolbarBinding.inflate(LayoutInflater.from(context), this, true)
        setListener()
    }

    private fun setListener() {
        binding.btnLeftOne.setOnClickListener { onBtnLeftOne() }
        binding.btnLeftTwo.setOnClickListener { onBtnLeftTwo() }
        binding.btnRightTwo.setOnClickListener { onBtnRightTwo() }
        binding.btnRightOne.setOnClickListener { onBtnRightOne() }
    }

    fun setSelectedListener(onBtnLeftOne: (() -> Unit), onBtnLeftTwo: (() -> Unit), onBtnRightTwo: (() -> Unit), onBtnRightOne: (() -> Unit)) {
        this.onBtnLeftOne = onBtnLeftOne
        this.onBtnLeftTwo = onBtnLeftTwo
        this.onBtnRightTwo = onBtnRightTwo
        this.onBtnRightOne = onBtnRightOne
    }

    fun setTitle(title: String) {
        binding.tvToolbarTitle.visibility = View.VISIBLE
        binding.tvToolbarTitle.text = title
    }

    fun setImageLeftOne(drawable: Drawable?) {
        if (drawable == null) {
            binding.btnLeftOne.visibility = View.INVISIBLE
        } else {
            binding.btnLeftOne.visibility = View.VISIBLE
            binding.btnLeftOne.setImageDrawable(drawable)
        }
    }

    fun setImageLeftTwo(drawable: Drawable?) {
        if (drawable == null) {
            binding.btnLeftTwo.visibility = View.INVISIBLE
        } else {
            binding.btnLeftTwo.visibility = View.VISIBLE
            binding.btnLeftTwo.setImageDrawable(drawable)
        }
    }

    fun setImageRightTwo(drawable: Drawable?) {
        if (drawable == null) {
            binding.btnRightTwo.visibility = View.INVISIBLE
        } else {
            binding.btnRightTwo.visibility = View.VISIBLE
            binding.btnRightTwo.setImageDrawable(drawable)
        }
    }

    fun setImageRightOne(drawable: Drawable?) {
        if (drawable == null) {
            binding.btnRightOne.visibility = View.INVISIBLE
        } else {
            binding.btnRightOne.visibility = View.VISIBLE
            binding.btnRightOne.setImageDrawable(drawable)
        }
    }

    override fun getRootView(): RelativeLayout? {
        return binding.rootView
    }

}