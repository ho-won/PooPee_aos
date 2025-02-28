package kr.co.ho1.poopee.common.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.databinding.DialogPermissionBinding

@SuppressLint("ValidFragment")
class PermissionDialog(private var onConfirm: (() -> Unit)) : BaseDialog() {
    private var _binding: DialogPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogPermissionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener {
            onConfirm()
            dismiss()
        }
    }

}