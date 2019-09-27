package kr.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_comment_report.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseDialog
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.home.model.Comment
import org.json.JSONException
import retrofit2.http.POST

@SuppressLint("ValidFragment")
class CommentReportDialog : BaseDialog() {
    private var mComment: Comment = Comment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_comment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    private fun init() {

    }

    private fun setListener() {
        btn_report.setOnClickListener {
            taskCommentReport()
        }

        btn_close.setOnClickListener {
            dismiss()
        }
    }

    fun setComment(comment: Comment) {
        this.mComment = comment
    }

    /**
     * [POST] 댓글신고
     */
    private fun taskCommentReport() {
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("comment_id", mComment.comment_id)
        params.put("content", edt_content.text)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).createCommentReport(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_report_complete), Toast.LENGTH_SHORT).show()
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