package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_comment_update.*
import kotlinx.android.synthetic.main.dialog_comment_update.btn_close
import kotlinx.android.synthetic.main.dialog_comment_update.edt_content
import kotlinx.android.synthetic.main.dialog_suggest.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.common.util.SleepTask
import kr.co.ho1.poopee.home.model.Comment
import org.json.JSONException
import retrofit2.http.PUT

@SuppressLint("ValidFragment")
class CommentUpdateDialog(private var onUpdate: ((comment: Comment) -> Unit)) : BaseDialog() {
    private var mComment: Comment = Comment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_comment_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    override fun dismiss() {
        MyUtil.keyboardHide(edt_content)
        super.dismiss()
    }

    private fun init() {
        edt_content.setText(mComment.content)
        SleepTask(100, onFinish = {
            edt_content.requestFocus()
            MyUtil.keyboardShow(edt_content)
        })
    }

    private fun setListener() {
        edt_content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    btn_update.setTextColor(Color.parseColor("#a0a4aa"))
                    btn_update.isEnabled = false
                } else {
                    btn_update.setTextColor(Color.parseColor("#2470ff"))
                    btn_update.isEnabled = true
                }
            }
        })
        btn_update.setOnClickListener {
            mComment.content = edt_content.text.toString()
            taskCommentUpdate()
        }

        btn_close.setOnClickListener {
            dismiss()
        }
    }

    fun setComment(comment: Comment) {
        this.mComment = comment
    }

    /**
     * [PUT] 댓글수정
     */
    private fun taskCommentUpdate() {
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("comment_id", mComment.comment_id)
        params.put("content", mComment.content)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).commentUpdate(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            onUpdate(mComment)
                            dismiss()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                onFailed = {

                }
        )
    }

}