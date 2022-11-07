package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.navi.Constants
import com.kakao.sdk.navi.NaviClient
import com.kakao.sdk.navi.model.CoordType
import com.kakao.sdk.navi.model.Location
import com.kakao.sdk.navi.model.NaviOption
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.*
import kotlinx.android.synthetic.main.dialog_share.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.home.model.Toilet


@Suppress("DEPRECATION")
@SuppressLint("ValidFragment")
class ShareDialog : BaseDialog() {
    companion object {
        const val TAG = "KAKAO_TEST"
        const val ACTION_NAVI = "ACTION_NAVI"
        const val ACTION_SHARE = "ACTION_SHARE"
    }

    private var mAction: String = ACTION_NAVI
    private var mToilet: Toilet = Toilet()
    private var mAddressText: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    private fun init() {
        if (mAction == ACTION_NAVI) {
            tv_title.text = MyUtil.getString(R.string.home_text_16)
            tv_content.text = MyUtil.getString(R.string.home_text_22)
            tv_01.text = MyUtil.getString(R.string.home_text_18)
            tv_02.text = MyUtil.getString(R.string.home_text_19)
            iv_01.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_tmap))
            iv_02.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_kakaonavi))
        } else if (mAction == ACTION_SHARE) {
            tv_title.text = MyUtil.getString(R.string.home_text_17)
            tv_content.text = MyUtil.getString(R.string.home_text_23)
            tv_01.text = MyUtil.getString(R.string.home_text_20)
            tv_02.text = MyUtil.getString(R.string.home_text_21)
            iv_01.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_kakaotalk))
            iv_02.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_sms))
        }

        mAddressText = ""

        when {
            mToilet.address_new.count() > 0 -> mAddressText = mToilet.address_new
            mToilet.address_old.count() > 0 -> mAddressText = mToilet.address_old
        }
    }

    private fun setListener() {
        layout_01.setOnClickListener {
            if (mAction == ACTION_NAVI) {
                try {
                    ObserverManager.root!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("tmap://route?goalx=${mToilet.longitude}&goaly=${mToilet.latitude}&goalname=${mAddressText}")).apply {
                        `package` = "com.skt.tmap.ku"
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                } catch (e: Exception) {
                    ObserverManager.root!!.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.skt.tmap.ku"))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            } else if (mAction == ACTION_SHARE) {
                val defaultLocation = LocationTemplate(
                    address = mAddressText,
                    addressTitle = mToilet.name,
                    content = Content(
                        title = MyUtil.getString(R.string.home_text_14) + mAddressText,
                        imageUrl = "http://poopee.ho1.co.kr/image/banner.png",
                        imageWidth = 1024,
                        imageHeight = 500,
                        link = Link(
                            webUrl = "http://poopee.ho1.co.kr/etcs/app_install",
                            mobileWebUrl = "http://poopee.ho1.co.kr/etcs/app_install"
                        ),
                    ),
                )

                // 카카오톡 설치여부 확인
                if (ShareClient.instance.isKakaoTalkSharingAvailable(ObserverManager.context!!)) {
                    // 카카오톡으로 카카오톡 공유 가능
                    ShareClient.instance.shareDefault(ObserverManager.context!!, defaultLocation) { sharingResult, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 공유 실패", error)
                        } else if (sharingResult != null) {
                            Log.d(TAG, "카카오톡 공유 성공 ${sharingResult.intent}")
                            startActivity(sharingResult.intent)

                            // 카카오톡 공유에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                            Log.w(TAG, "Warning Msg: ${sharingResult.warningMsg}")
                            Log.w(TAG, "Argument Msg: ${sharingResult.argumentMsg}")
                        }
                    }
                } else {
                    // 카카오톡 미설치: 웹 공유 사용 권장
                    // 웹 공유 예시 코드
                    val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultLocation)

                    // CustomTabs으로 웹 브라우저 열기

                    // 1. CustomTabsServiceConnection 지원 브라우저 열기
                    // ex) Chrome, 삼성 인터넷, FireFox, 웨일 등
                    try {
                        KakaoCustomTabsClient.openWithDefault(ObserverManager.context!!, sharerUrl)
                    } catch (e: UnsupportedOperationException) {
                        // CustomTabsServiceConnection 지원 브라우저가 없을 때 예외처리
                    }

                    // 2. CustomTabsServiceConnection 미지원 브라우저 열기
                    // ex) 다음, 네이버 등
                    try {
                        KakaoCustomTabsClient.open(ObserverManager.context!!, sharerUrl)
                    } catch (e: ActivityNotFoundException) {
                        // 디바이스에 설치된 인터넷 브라우저가 없을 때 예외처리
                    }
                }
            }
        }
        layout_02.setOnClickListener {
            if (mAction == ACTION_NAVI) {
                // 카카오내비 앱으로 길 안내
                if (NaviClient.instance.isKakaoNaviInstalled(requireContext())) {
                    // 카카오내비 앱으로 길 안내 - WGS84
                    startActivity(
                        NaviClient.instance.navigateIntent(
                            Location(mAddressText, mToilet.longitude.toString(), mToilet.latitude.toString()),
                            NaviOption(coordType = CoordType.WGS84)
                        )
                    )
                } else {
                    // 카카오내비 설치 페이지로 이동
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(Constants.WEB_NAVI_INSTALL)
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                }
            } else if (mAction == ACTION_SHARE) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.type = "vnd.android-dir/mms-sms"
                intent.putExtra("sms_body", MyUtil.getString(R.string.home_text_14) + mAddressText)
                startActivity(intent)
            }
        }
        btn_close.setOnClickListener {
            dismiss()
        }
    }

    fun setAction(action: String) {
        this.mAction = action
    }

    fun setToilet(toilet: Toilet) {
        this.mToilet = toilet
    }

}