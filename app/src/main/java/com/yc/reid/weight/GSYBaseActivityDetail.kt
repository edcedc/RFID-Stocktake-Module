/*
package com.yc.yyc.weight

import android.content.res.Configuration
import android.os.Bundle
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.yc.yyc.base.BaseActivity

*/
/**
 * 详情模式播放页面基础类
 * Created by guoshuyu on 2017/9/14.
 *//*

abstract class GSYBaseActivityDetail<T : GSYBaseVideoPlayer> : BaseActivity(), VideoAllCallBack {

    protected var isPlay: Boolean = false

    protected var isPause: Boolean = false

    protected var orientationUtils: OrientationUtils? = null

    */
/**
     * 播放控件
     *//*

    abstract val gsyVideoPlayer: T

    */
/**
     * 配置播放器
     *//*

    abstract val gsyVideoOptionBuilder: GSYVideoOptionBuilder

    */
/**
     * 是否启动旋转横屏，true表示启动
     *//*

    abstract val detailOrientationRotateAuto: Boolean

    */
/**
     * 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，注意，这时候默认旋转无效
     *//*

    val isAutoFullWithSize: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    */
/**
     * 选择普通模式
     *//*

    fun initVideo() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, gsyVideoPlayer)
        //初始化不打开外部的旋转
        orientationUtils!!.isEnable = false
        if (gsyVideoPlayer.fullscreenButton != null) {
            gsyVideoPlayer.fullscreenButton.setOnClickListener {
                showFull()
                clickForFullScreen()
            }
        }
    }

    */
/**
     * 选择builder模式
     *//*

    fun initVideoBuilderMode() {
        initVideo()
        gsyVideoOptionBuilder.setVideoAllCallBack(this)
            .build(gsyVideoPlayer)
    }

    fun showFull() {
        if (orientationUtils!!.isLand != 1) {
            //直接横屏
            orientationUtils!!.resolveByClick()
        }
        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
        gsyVideoPlayer.startWindowFullscreen(act, hideActionBarWhenFull(), hideStatusBarWhenFull())

    }

    override fun onBackPressedSupport() {
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressedSupport()
    }

    override fun onPause() {
        super.onPause()
        gsyVideoPlayer.currentPlayer.onVideoPause()
        if (orientationUtils != null) {
//            orientationUtils!!.setIsPause(true)
        }
        isPause = true
    }

    override fun onResume() {
        super.onResume()
        gsyVideoPlayer.currentPlayer.onVideoResume()
        if (orientationUtils != null) {
//            orientationUtils!!.setIsPause(false)
        }
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            gsyVideoPlayer.currentPlayer.release()
        }
        if (orientationUtils != null)
            orientationUtils!!.releaseListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            gsyVideoPlayer.onConfigurationChanged(
                this,
                newConfig,
                orientationUtils,
                hideActionBarWhenFull(),
                hideStatusBarWhenFull()
            )
        }
    }

    override fun onStartPrepared(url: String, vararg objects: Any) {

    }

    override fun onPrepared(url: String, vararg objects: Any) {

        if (orientationUtils == null) {
            throw NullPointerException("initVideo() or initVideoBuilderMode() first")
        }
        //开始播放了才能旋转和全屏
        orientationUtils!!.isEnable = detailOrientationRotateAuto && !isAutoFullWithSize
        isPlay = true
    }

    override fun onClickStartIcon(url: String, vararg objects: Any) {

    }

    override fun onClickStartError(url: String, vararg objects: Any) {

    }

    override fun onClickStop(url: String, vararg objects: Any) {

    }

    override fun onClickStopFullscreen(url: String, vararg objects: Any) {

    }

    override fun onClickResume(url: String, vararg objects: Any) {

    }

    override fun onClickResumeFullscreen(url: String, vararg objects: Any) {

    }

    override fun onClickSeekbar(url: String, vararg objects: Any) {

    }

    override fun onClickSeekbarFullscreen(url: String, vararg objects: Any) {

    }

    override fun onAutoComplete(url: String, vararg objects: Any) {

    }

    override fun onEnterFullscreen(url: String, vararg objects: Any) {

    }

    override fun onQuitFullscreen(url: String, vararg objects: Any) {
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
    }

    override fun onQuitSmallWidget(url: String, vararg objects: Any) {

    }

    override fun onEnterSmallWidget(url: String, vararg objects: Any) {

    }

    override fun onTouchScreenSeekVolume(url: String, vararg objects: Any) {

    }

    override fun onTouchScreenSeekPosition(url: String, vararg objects: Any) {

    }

    override fun onTouchScreenSeekLight(url: String, vararg objects: Any) {

    }

    override fun onPlayError(url: String, vararg objects: Any) {

    }

    override fun onClickStartThumb(url: String, vararg objects: Any) {

    }

    override fun onClickBlank(url: String, vararg objects: Any) {

    }

    override fun onClickBlankFullscreen(url: String, vararg objects: Any) {

    }

    fun hideActionBarWhenFull(): Boolean {
        return true
    }

    fun hideStatusBarWhenFull(): Boolean {
        return true
    }

    */
/**
     * 点击了全屏
     *//*

    abstract fun clickForFullScreen()
}
*/
