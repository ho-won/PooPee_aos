package kr.ho1.poopee.home.view

import android.content.Context
import android.support.design.widget.NavigationView
import android.util.AttributeSet
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.nav_main.view.*
import kr.ho1.poopee.R

class NavMainView : NavigationView {
    private var mContext: Context? = null

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
        this.mContext = context
        LayoutInflater.from(context).inflate(R.layout.nav_main, this, true)
        setListener()
    }

    private fun setListener() {
        btn_login.setOnClickListener {

        }
        btn_my_info.setOnClickListener {

        }
        btn_notice.setOnClickListener {

        }
        btn_request.setOnClickListener {

        }
    }

}