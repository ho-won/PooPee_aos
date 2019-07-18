package kr.ho1.poopee.common

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.base.BaseApp
import kr.ho1.poopee.common.util.SleepTask
import kr.ho1.poopee.home.HomeActivity

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
                                startActivity(Intent(ObserverManager.context!!, HomeActivity::class.java)
                                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                                )
                                finish()
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

}
