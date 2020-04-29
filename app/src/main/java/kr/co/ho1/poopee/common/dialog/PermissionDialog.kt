package kr.co.ho1.poopee.common.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_permission.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseDialog

@SuppressLint("ValidFragment")
class PermissionDialog(private var onConfirm: (() -> Unit)) : BaseDialog() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_confirm.setOnClickListener {
            onConfirm()
            dismiss()
        }
    }

}