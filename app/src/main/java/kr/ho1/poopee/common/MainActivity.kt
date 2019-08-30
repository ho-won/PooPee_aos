package kr.ho1.poopee.common

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.base.BaseApp
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.dialog.BasicDialog
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.PermissionManager
import kr.ho1.poopee.common.util.SleepTask
import kr.ho1.poopee.home.HomeActivity
import org.json.JSONException
import org.json.JSONObject

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        val animation = AnimationUtils.loadAnimation(ObserverManager.context, R.anim.intro_drop)
        animation.interpolator = AccelerateInterpolator(0.5f)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                val animationUp = AnimationUtils.loadAnimation(ObserverManager.context, R.anim.intro_drop_up)
                animationUp.interpolator = AccelerateInterpolator(0.5f)
                animationUp.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        if (intent.action != null && intent.action == BaseApp.ACTION_EXIT) {
                            // 앱종료
                            finish()
                        } else {
                            val sleepTask = SleepTask(1000, onFinish = {
                                // 퍼미션 체크
                                if (PermissionManager.permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                    taskServerCheck()
                                } else {
                                    PermissionManager.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PermissionManager.ACCESS_FINE_LOCATION)
                                }
                            })
                            sleepTask.execute()
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
                layout_drop.startAnimation(animationUp)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        layout_drop.startAnimation(animation)
    }

    /**
     * 서버의 앱버전정보와 현재 앱버전정보를 비교하여 팝업창 띄움.
     */
    private fun onVersionCheck(response: JSONObject) {
        try {
            val version = response.getString("version_aos")
            val versions = version.split(".")
            val appVersions = BaseApp.APP_VERSION.split(".")

            when {
                Integer.parseInt(versions[0]) > Integer.parseInt(appVersions[0]) -> {
                    // 첫번째 자리 버전 업데이트로 무조건 업데이트받아야 앱 실행
                    val dialog = BasicDialog(
                            onLeftButton = {
                                finish()
                            },
                            onCenterButton = {

                            },
                            onRightButton = {
                                updateInPlayMarket()
                                finish()
                            }
                    )
                    dialog.isCancelable = false
                    dialog.setTextContent(ObserverManager.context!!.resources.getString(R.string.dialog_force_new_version_update))
                    dialog.setBtnLeft(ObserverManager.context!!.resources.getString(R.string.no))
                    dialog.setBtnRight(ObserverManager.context!!.resources.getString(R.string.yes))
                    dialog.show(supportFragmentManager, "BasicDialog")
                }
                Integer.parseInt(versions[1]) > Integer.parseInt(appVersions[1]) -> {
                    // 두번째 자리 버전 업데이트로 업데이트 팝업 선택 후 앱 실행
                    val dialog = BasicDialog(
                            onLeftButton = {
                                gotoHomeActivity()
                            },
                            onCenterButton = {

                            },
                            onRightButton = {
                                updateInPlayMarket()
                                finish()
                            }
                    )
                    dialog.isCancelable = false
                    dialog.setTextContent(ObserverManager.context!!.resources.getString(R.string.dialog_assign_new_version_update))
                    dialog.setBtnLeft(ObserverManager.context!!.resources.getString(R.string.no))
                    dialog.setBtnRight(ObserverManager.context!!.resources.getString(R.string.yes))
                    dialog.show(supportFragmentManager, "BasicDialog")
                }
                Integer.parseInt(versions[0]) < Integer.parseInt(appVersions[0]) -> // 세번째 자리 버전 업데이트로 무시하고 앱실행
                    gotoHomeActivity()
                else -> // 현재 앱버전이 최신버전일경우
                    gotoHomeActivity()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * 앱의 플레이스토어로 이동.
     */
    private fun updateInPlayMarket() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
    }

    /**
     * 홈 화면으로 이동.
     */
    private fun gotoHomeActivity() {
        startActivity(Intent(ObserverManager.context!!, HomeActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        )
        finish()
    }

    private fun taskServerCheck() {
        val params = RetrofitParams()
        params.put("date", SharedManager.getNoticeDate())

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).serverCheck(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getString("server") == "success") {
                            onVersionCheck(it) // 버전체크
                        } else {
                            Toast.makeText(ObserverManager.context!!, it.getString("server"), Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_checking_service), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                },
                onFailed = {
                    Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_checking_service), Toast.LENGTH_SHORT).show()
                    finish()
                }
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var result = 0
        for (grantResult in grantResults) {
            result += grantResult
        }

        if (result == 0) {
            when (requestCode) {
                PermissionManager.ACCESS_FINE_LOCATION -> {
                    startActivity(Intent(ObserverManager.context!!, HomeActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    )
                    finish()
                }
            }
        } else {
            Toast.makeText(ObserverManager.context, ObserverManager.context!!.resources.getString(R.string.toast_please_permission), Toast.LENGTH_SHORT).show()
        }
    }

}
