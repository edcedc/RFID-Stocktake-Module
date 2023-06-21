/*
package com.yc.yyc.ui.act

import android.os.Bundle
import android.widget.ImageView
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.model.GSYVideoModel
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer
import com.yc.yyc.R
import com.yc.yyc.weight.GSYBaseActivityDetail
import com.yc.yyc.weight.GlideLoadingUtils
import kotlinx.android.synthetic.main.a_video.*
import java.util.ArrayList

*/
/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/10/20
 * Time: 18:26
 *  视频播放
 *//*

class VideoAct :  GSYBaseActivityDetail<ListGSYVideoPlayer>() {

    var videoUrl: String? = null

    var iamgeUrl: String? = null

    override fun getLayoutId(): Int = R.layout.a_video

    override fun initParms(bundle: Bundle) {
        videoUrl = bundle.getString("video")//视频 + 封面图
        iamgeUrl = bundle.getString("image")
    }

    override fun initView() {
        setSofia(true)

        //普通模式
        initVideo()
        val urls = ArrayList<GSYVideoModel>()
        urls.add(GSYVideoModel(videoUrl, ""))
        video_play.setUp(urls, true, 0)

        //增加title
//        detailPlayer.getTitleTextView().setVisibility(View.VISIBLE)
//        detailPlayer.getBackButton().setVisibility(View.VISIBLE)
        video_play.backButton.setOnClickListener {
            finish()
        }

        //增加封面
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        GlideLoadingUtils.load(act, iamgeUrl, imageView)
        video_play.setThumbImageView(imageView)

        video_play.setIsTouchWiget(true)
        //关闭自动旋转
        video_play.setRotateViewAuto(false)
        video_play.setLockLand(false)
        video_play.setShowFullAnimation(false)
        video_play.setNeedLockFull(true);
        video_play.setAutoFullWithSize(true)

        video_play.setVideoAllCallBack(this)

        video_play.setLockClickListener({ view, lock ->
            if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                orientationUtils!!.isEnable = !lock
            }
        })

//        next.setOnClickListener(View.OnClickListener { (detailPlayer.getCurrentPlayer() as ListGSYVideoPlayer).playNext() })
    }

    override fun clickForFullScreen() {
    }

    override val gsyVideoPlayer: ListGSYVideoPlayer
        get() = video_play


    override val detailOrientationRotateAuto: Boolean
        get() = true

    override val gsyVideoOptionBuilder: GSYVideoOptionBuilder
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.


}*/
