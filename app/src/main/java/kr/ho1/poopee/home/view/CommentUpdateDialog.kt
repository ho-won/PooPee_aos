package kr.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_comment_update.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.base.BaseDialog
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.home.model.Comment
import org.json.JSONException

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

    private fun init() {
        edt_content.setText(mComment.content)
    }

    private fun setListener() {
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

    private fun taskCommentUpdate() {
        val params = RetrofitParams()
        params.put("comment_id", mComment.id)
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