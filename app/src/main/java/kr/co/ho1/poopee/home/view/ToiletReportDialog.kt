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
import kotlinx.android.synthetic.main.dialog_comment_report.*
import kotlinx.android.synthetic.main.dialog_comment_report.btn_close
import kotlinx.android.synthetic.main.dialog_comment_report.edt_content
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.home.model.Toilet
import org.json.JSONException
import retrofit2.http.POST

@SuppressLint("ValidFragment")
class ToiletReportDialog : BaseDialog() {
    private var mToilet: Toilet = Toilet()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_comment_report, container, false)
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

    }

    private fun setListener() {
        edt_content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    btn_report.setTextColor(Color.parseColor("#a0a4aa"))
                    btn_report.isEnabled = false
                } else {
                    btn_report.setTextColor(Color.parseColor("#2470ff"))
                    btn_report.isEnabled = true
                }
            }
        })
        btn_report.setOnClickListener {
            taskToiletReport()
        }

        btn_close.setOnClickListener {
            dismiss()
        }
    }

    fun setToilet(toilet: Toilet) {
        this.mToilet = toilet
    }

    /**
     * [POST] 화장실신고
     */
    private fun taskToiletReport() {
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("toilet_id", mToilet.toilet_id)
        params.put("content", edt_content.text)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).createToiletReport(params.getParams())

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