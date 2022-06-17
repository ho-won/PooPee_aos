package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_comment_create.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.common.util.SleepTask

@SuppressLint("ValidFragment")
class CommentCreateDialog(private var onCreate: ((comment: String) -> Unit)) : BaseDialog() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_comment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    override fun dismiss() {
        MyUtil.keyboardHide(edt_comment)
        super.dismiss()
    }

    private fun init() {
        btn_send.isEnabled = false
        SleepTask(100, onFinish = {
            edt_comment.requestFocus()
            MyUtil.keyboardShow(edt_comment)
        })
    }

    private fun setListener() {
        edt_comment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    btn_comment_delete.visibility = View.GONE
                    btn_send.isEnabled = false
                } else {
                    btn_comment_delete.visibility = View.VISIBLE
                    btn_send.isEnabled = true
                }
            }
        })
        btn_comment_delete.setOnClickListener {
            edt_comment.setText("")
        }
        btn_send.setOnClickListener {
            onCreate(edt_comment.text.toString())
            dismiss()
        }
    }

}