package com.yc.reid.utils

import android.os.Handler
import android.os.Message
import android.os.SystemClock


abstract class CountDownTimer
/**
 * @param millisInFuture    The number of millis in the future from the call
 * to [.start] until the countdown is done and [.onFinish]
 * is called.
 * @param countDownInterval The interval along the way to receive
 * [.onTick] callbacks.
 */
    (
    /**
     * Millis since epoch when alarm should stop.
     */
    private val mMillisInFuture: Long,
    /**
     * The interval in millis that the user receives callbacks
     */
    private val mCountdownInterval: Long
) {

    private var mMillisFinished: Long = 0

    private var mElapsedRealtime: Long = 0

    /**
     * boolean representing if the timer was cancelled
     */
    private var mCancelled = false

    /**
     * 获得当前的时间long.
     */
    val nowTime: Long
        @Synchronized get() = mMillisFinished + SystemClock.elapsedRealtime() - mElapsedRealtime


    // handles counting down
    private val mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {

            synchronized(this@CountDownTimer) {
                if (mCancelled) {
                    return
                }
                val elapsedRealtime = mElapsedRealtime
                mElapsedRealtime = SystemClock.elapsedRealtime()
                val millis = mElapsedRealtime - elapsedRealtime
                mMillisFinished += millis

                if (mMillisInFuture <= mMillisFinished) {
                    onFinish(mMillisFinished)
                } else {
                    onTick(mMillisFinished)

                    var delay = mMillisInFuture - mMillisFinished
                    if (delay > mCountdownInterval) {

                        // take into account user's onTick taking time to execute
                        delay =
                            mElapsedRealtime + mCountdownInterval - SystemClock.elapsedRealtime() - mMillisFinished % mCountdownInterval

                    }

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0)
                        delay += mCountdownInterval

                    sendMessageDelayed(obtainMessage(MSG), delay)
                }
            }
        }
    }

    /**
     * Cancel the countdown.
     */
    @Synchronized
    fun cancel() {
        mCancelled = true
        mHandler.removeMessages(MSG)
    }

    /**
     * Stop the countdown.
     */
    @Synchronized
    fun stop(): CountDownTimer {
        mHandler.removeMessages(MSG)

        val elapsedRealtime = mElapsedRealtime
        mElapsedRealtime = SystemClock.elapsedRealtime()
        val millis = mElapsedRealtime - elapsedRealtime
        mMillisFinished += millis

        onTick(mMillisFinished)
        return this
    }

    /**
     * Start the countdown.
     */
    @Synchronized
    fun start(): CountDownTimer {

        mCancelled = false
        if (mMillisInFuture <= mMillisFinished) {
            onFinish(mMillisFinished)
            return this
        }
        mElapsedRealtime = SystemClock.elapsedRealtime()
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
        return this
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    abstract fun onTick(millisUntilFinished: Long)

    /**
     * Callback fired when the time is up.
     */
    abstract fun onFinish(millisUntilFinished: Long)

    companion object {


        private val MSG = 1
    }
}