package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.android.synthetic.main.dialog_finish.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseDialog

@SuppressLint("ValidFragment")
class FinishDialog : BaseDialog() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_finish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    private fun init() {
        ad_view.loadAd(AdRequest.Builder().build())
    }

    private fun setListener() {
        btn_no.setOnClickListener {
            val manager = ReviewManagerFactory.create(ObserverManager.context!!)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener {
                if (it.isSuccessful) {
                    ObserverManager.root!!.finish()
                }
            }
        }
        btn_yes.setOnClickListener {
            ObserverManager.root!!.finish()
        }
    }

}