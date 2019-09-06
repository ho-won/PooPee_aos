package kr.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_popup.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.base.BaseDialog
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.PicassoManager
import kr.ho1.poopee.common.util.StrManager

@SuppressLint("ValidFragment")
class PopupDialog : BaseDialog() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    private fun init() {
        Picasso.get()
                .load(PicassoManager.getImageUrl(RetrofitService.Companion.BASE_APP + SharedManager.getNoticeImage()))
                .into(iv_popup)
    }

    private fun setListener() {
        btn_show.setOnClickListener {
            SharedManager.setNoticeImage("")
            SharedManager.setNoticeDate(StrManager.getCurrentDate())
            dismiss()
        }

        btn_close.setOnClickListener {
            dismiss()
        }
    }

}