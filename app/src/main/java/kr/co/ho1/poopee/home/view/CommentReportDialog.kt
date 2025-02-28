package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.databinding.DialogCommentReportBinding
import kr.co.ho1.poopee.home.model.Comment
import org.json.JSONException
import retrofit2.http.POST

@SuppressLint("ValidFragment")
class CommentReportDialog : BaseDialog() {
    private var _binding: DialogCommentReportBinding? = null
    private val binding get() = _binding!!

    private var mComment: Comment = Comment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogCommentReportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    override fun dismiss() {
        MyUtil.keyboardHide(binding.edtContent)
        super.dismiss()
    }

    private fun init() {

    }

    private fun setListener() {
        binding.edtContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    binding.btnReport.setTextColor(Color.parseColor("#a0a4aa"))
                    binding.btnReport.isEnabled = false
                } else {
                    binding.btnReport.setTextColor(Color.parseColor("#2470ff"))
                    binding.btnReport.isEnabled = true
                }
            }
        })
        binding.btnReport.setOnClickListener {
            taskCommentReport()
        }

        binding.btnClose.setOnClickListener {
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
        params.put("content", binding.edtContent.text)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).createCommentReport(params.getParams())

        RetrofitJSONObject(request,
            onSuccess = {
                try {
                    if (it.getInt("rst_code") == 0) {
                        Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_report_complete), Toast.LENGTH_SHORT).show()
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