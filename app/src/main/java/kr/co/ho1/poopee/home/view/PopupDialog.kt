package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.StrManager
import kr.co.ho1.poopee.databinding.DialogPopupBinding
import java.util.*

@SuppressLint("ValidFragment")
class PopupDialog : BaseDialog() {
    private var _binding: DialogPopupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogPopupBinding.inflate(layoutInflater)
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

    private fun init() {
        var imageName = SharedManager.noticeImage
        if (Locale.getDefault().language != "ko") {
            val array = imageName.split(".")
            imageName = array[0] + "_en." + array[1]
        }
        Glide.with(this)
            .load(RetrofitService.BASE_APP + imageName)
            .into(binding.ivPopup)
    }

    private fun setListener() {
        binding.btnShow.setOnClickListener {
            SharedManager.noticeImage = ""
            SharedManager.noticeDate = StrManager.getCurrentDate()
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

}