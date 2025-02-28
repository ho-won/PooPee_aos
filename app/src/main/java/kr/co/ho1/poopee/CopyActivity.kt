package kr.co.ho1.poopee

import android.os.Bundle
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.databinding.ActivityCopyBinding
import org.json.JSONException

@Suppress("DEPRECATION")
class CopyActivity : BaseActivity() {
    private lateinit var binding: ActivityCopyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        init()
        setListener()
    }

    private fun init() {

    }

    private fun setListener() {

    }

    private fun task() {
        val params = RetrofitParams()
        params.put("", "")

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).test(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                onFailed = {

                }
        )
    }

    override fun setToolbar() {
        binding.toolbar.setImageLeftOne(MyUtil.getDrawable(R.drawable.ic_navigationbar_back))
        binding.toolbar.setSelectedListener(
                onBtnLeftOne = {
                    finish()
                },
                onBtnLeftTwo = {

                },
                onBtnRightOne = {

                },
                onBtnRightTwo = {

                }
        )
    }

}