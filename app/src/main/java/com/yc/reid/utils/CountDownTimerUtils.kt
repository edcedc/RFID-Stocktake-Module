package com.yc.reid.utils

import android.content.Context
import android.os.CountDownTimer
import android.widget.TextView

/**
 * Created by edison on 2018/4/12.
 */

class CountDownTimerUtils
/**
 * @param millisInFuture    The number of millis in the future from the call
 * to [.start] until the countdown is done and [.onFinish]
 * is called.
 * @param countDownInterval The interval along the way to receive
 * [.onTick] callbacks.
 */
    (
    private val act: Context?,
    millisInFuture: Long,
    countDownInterval: Long,
    private val view: TextView
)//        this.mCountDownInterval = millisInFuture;
//        this.mCountDownInterval = countDownInterval;
    : CountDownTimer(millisInFuture, countDownInterval) {

    private val mMillisInFuture: Long = 6000

    private val mCountDownInterval: Long = 1000

    override fun onTick(millisUntilFinished: Long) {
        view.isEnabled = false
        view.setEms(6)
        view.text = "已发送(" + millisUntilFinished / 1000 + "s)"
    }

    override fun onFinish() {
        if (act != null) {
            view.isEnabled = true
            view.setEms(6)
            view.text = "获取验证码"
        }
    }
}
