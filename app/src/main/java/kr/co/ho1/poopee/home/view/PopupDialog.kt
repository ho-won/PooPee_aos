package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_popup.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.PicassoManager
import kr.co.ho1.poopee.common.util.StrManager
import java.util.*

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
        var imageName = SharedManager.getNoticeImage()
        if (Locale.getDefault().language != "ko") {
            val array = imageName.split(".")
            imageName = array[0] + "_en." + array[1]
        }
        Glide.with(this)
            .load(RetrofitService.BASE_APP + imageName)
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