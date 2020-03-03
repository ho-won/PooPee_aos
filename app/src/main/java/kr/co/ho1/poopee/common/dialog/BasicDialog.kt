package kr.co.ho1.poopee.common.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_basic.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseDialog

@SuppressLint("ValidFragment")
class BasicDialog(private var onLeftButton: (() -> Unit), private var onCenterButton: (() -> Unit), private var onRightButton: (() -> Unit)) : BaseDialog() {
    private var textTitle: String? = null // 제목
    private var textContent: String? = null // 내용
    private var textContentSub: String? = null // 서브내용
    private var btnLeft: String? = null
    private var btnRight: String? = null
    private var btnCenter: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_basic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (textTitle != null) {
            tv_title.text = textTitle
            tv_title.visibility = View.VISIBLE
        } else {
            tv_title.visibility = View.GONE
        }

        if (textContent != null) {
            tv_content.text = textContent
            tv_content.visibility = View.VISIBLE
        } else {
            tv_content.visibility = View.GONE
        }

        if (textContentSub != null) {
            tv_content_sub.text = textContentSub
            tv_content_sub.visibility = View.VISIBLE
        } else {
            tv_content_sub.visibility = View.GONE
        }

        if (textContentSub != null) {
            tv_content_sub.text = textContentSub
            tv_content_sub.visibility = View.VISIBLE
        } else {
            tv_content_sub.visibility = View.GONE
        }

        if (btnCenter != null) {
            btn_center.text = btnCenter
            btn_center.visibility = View.VISIBLE
        } else {
            btn_center.visibility = View.GONE
        }

        btn_left.text = btnLeft
        btn_right.text = btnRight

        btn_left.setOnClickListener {
            onLeftButton()
            dismiss()
        }

        btn_center.setOnClickListener {
            onCenterButton()
            dismiss()
        }

        btn_right.setOnClickListener {
            onRightButton()
            dismiss()
        }
    }

    fun setTextTitle(textTitle: String) {
        this.textTitle = textTitle
    }

    fun setTextContent(textContent: String) {
        this.textContent = textContent
    }

    fun setTextContentSub(textContentSub: String) {
        this.textContentSub = textContentSub
    }

    fun setBtnLeft(btnLeft: String) {
        this.btnLeft = btnLeft
    }

    fun setBtnRight(btnRight: String) {
        this.btnRight = btnRight
    }

    fun setBtnCenter(btnCenter: String) {
        this.btnCenter = btnCenter
    }

}