package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.common.util.SleepTask
import kr.co.ho1.poopee.databinding.DialogCommentCreateBinding

@SuppressLint("ValidFragment")
class CommentCreateDialog(private var onCreate: ((comment: String) -> Unit)) : BaseDialog() {
    private var _binding: DialogCommentCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogCommentCreateBinding.inflate(inflater, container, false)
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
        MyUtil.keyboardHide(binding.edtComment)
        super.dismiss()
    }

    private fun init() {
        binding.btnSend.isEnabled = false
        SleepTask(100, onFinish = {
            binding.edtComment.requestFocus()
            MyUtil.keyboardShow(binding.edtComment)
        })
    }

    private fun setListener() {
        binding.edtComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    binding.btnCommentDelete.visibility = View.GONE
                    binding.btnSend.isEnabled = false
                } else {
                    binding.btnCommentDelete.visibility = View.VISIBLE
                    binding.btnSend.isEnabled = true
                }
            }
        })
        binding.btnCommentDelete.setOnClickListener {
            binding.edtComment.setText("")
        }
        binding.btnSend.setOnClickListener {
            onCreate(binding.edtComment.text.toString())
            dismiss()
        }
    }

}