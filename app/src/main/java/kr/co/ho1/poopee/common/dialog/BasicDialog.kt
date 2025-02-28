package kr.co.ho1.poopee.common.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.databinding.DialogBasicBinding

@SuppressLint("ValidFragment")
class BasicDialog(private var onLeftButton: (() -> Unit), private var onCenterButton: (() -> Unit), private var onRightButton: (() -> Unit)) : BaseDialog() {
    private var _binding: DialogBasicBinding? = null
    private val binding get() = _binding!!

    private var textTitle: String? = null // 제목
    private var textContent: String? = null // 내용
    private var textContentSub: String? = null // 서브내용
    private var btnLeft: String? = null
    private var btnRight: String? = null
    private var btnCenter: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogBasicBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (textTitle != null) {
            binding.tvTitle.text = textTitle
            binding.tvTitle.visibility = View.VISIBLE
        } else {
            binding.tvTitle.visibility = View.GONE
        }

        if (textContent != null) {
            binding.tvContent.text = textContent
            binding.tvContent.visibility = View.VISIBLE
        } else {
            binding.tvContent.visibility = View.GONE
        }

        if (textContentSub != null) {
            binding.tvContentSub.text = textContentSub
            binding.tvContentSub.visibility = View.VISIBLE
        } else {
            binding.tvContentSub.visibility = View.GONE
        }

        if (btnCenter != null) {
            binding.btnCenter.text = btnCenter
            binding.btnCenter.visibility = View.VISIBLE
        } else {
            binding.btnCenter.visibility = View.GONE
        }

        binding.btnLeft.text = btnLeft
        binding.btnRight.text = btnRight

        binding.btnLeft.setOnClickListener {
            onLeftButton()
            dismiss()
        }

        binding.btnCenter.setOnClickListener {
            onCenterButton()
            dismiss()
        }

        binding.btnRight.setOnClickListener {
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