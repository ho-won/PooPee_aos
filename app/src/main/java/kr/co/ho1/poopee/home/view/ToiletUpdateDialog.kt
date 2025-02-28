package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
import kr.co.ho1.poopee.databinding.DialogToiletCreateBinding
import kr.co.ho1.poopee.home.model.Toilet
import org.json.JSONException

@SuppressLint("ValidFragment")
class ToiletUpdateDialog(private var toilet: Toilet, private var onUpdate: ((toilet: Toilet) -> Unit)) : BaseDialog() {
    private var _binding: DialogToiletCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogToiletCreateBinding.inflate(layoutInflater)
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
        binding.btnSend.text = MyUtil.getString(R.string.modified)

        binding.edtTitle.setText(toilet.name)
        binding.edtContent.setText(toilet.content)
        binding.tvContentCnt.text = toilet.content.length.toString() + "/100"

        if (toilet.m_poo.toInt() > 0) {
            binding.cbMan.isChecked = true
            binding.cbWoman.isChecked = false
            binding.cbPublic.isChecked = false
        } else if (toilet.w_poo.toInt() > 0) {
            binding.cbMan.isChecked = false
            binding.cbWoman.isChecked = true
            binding.cbPublic.isChecked = false
        } else {
            binding.cbMan.isChecked = false
            binding.cbWoman.isChecked = false
            binding.cbPublic.isChecked = true
        }
    }

    private fun setListener() {
        binding.edtTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty() && isGubunChecked()) {
                    binding.btnSend.setTextColor(Color.parseColor("#2470ff"))
                    binding.btnSend.isEnabled = true
                } else {
                    binding.btnSend.setTextColor(Color.parseColor("#a0a4aa"))
                    binding.btnSend.isEnabled = false
                }
            }
        })
        binding.edtContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvContentCnt.text = p0.toString().length.toString() + "/100"
            }
        })
        binding.cbPublic.setOnClickListener {
            setGubunCheck(binding.cbPublic)
        }
        binding.cbMan.setOnClickListener {
            setGubunCheck(binding.cbMan)
        }
        binding.cbWoman.setOnClickListener {
            setGubunCheck(binding.cbWoman)
        }
        binding.btnSend.setOnClickListener {
            taskUpdateToilet()
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setGubunCheck(checkBox: CheckBox) {
        binding.cbPublic.isChecked = false
        binding.cbMan.isChecked = false
        binding.cbWoman.isChecked = false
        checkBox.isChecked = true
        if (binding.edtTitle.text.toString().isNotEmpty()) {
            binding.btnSend.setTextColor(Color.parseColor("#2470ff"))
            binding.btnSend.isEnabled = true
        } else {
            binding.btnSend.setTextColor(Color.parseColor("#a0a4aa"))
            binding.btnSend.isEnabled = false
        }
    }

    private fun isGubunChecked(): Boolean {
        return binding.cbPublic.isChecked || binding.cbMan.isChecked || binding.cbWoman.isChecked
    }

    /**
     * [POST] 화장실추가
     */
    private fun taskUpdateToilet() {
        toilet.name = binding.edtTitle.text.toString()
        toilet.content = binding.edtContent.text.toString()

        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("toilet_id", toilet.toilet_id)
        params.put("name", toilet.name) // 화장실명
        params.put("content", toilet.content) // 화장실설명

        when {
            binding.cbMan.isChecked -> {
                params.put("type", 1) // 0(공용) 1(남자) 2(여자)
                toilet.m_poo = "1"
                toilet.w_poo = "0"
                toilet.unisex = "N"
            }

            binding.cbWoman.isChecked -> {
                params.put("type", 2) // 0(공용) 1(남자) 2(여자)
                toilet.m_poo = "0"
                toilet.w_poo = "1"
                toilet.unisex = "N"
            }

            else -> {
                params.put("type", 0) // 0(공용) 1(남자) 2(여자)
                toilet.m_poo = "0"
                toilet.w_poo = "0"
                toilet.unisex = "Y"
            }
        }

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).toiletUpdate(params.getParams())

        RetrofitJSONObject(request,
            onSuccess = {
                try {
                    if (it.getInt("rst_code") == 0) {
                        Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_update_complete), Toast.LENGTH_SHORT).show()
                        onUpdate(toilet)
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