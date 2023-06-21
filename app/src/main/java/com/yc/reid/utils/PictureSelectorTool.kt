package com.yc.reid.utils

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.luck.picture.lib.basic.IBridgeViewLifecycle
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.InjectResourceSource
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnInjectActivityPreviewListener
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.style.PictureSelectorStyle
import com.yc.reid.R
import com.yc.reid.weight.GlideEngine

/**
 * Created by yc on 2018/1/23.
 * https://github.com/LuckSiege/PictureSelector  不会配置看这里
 */

object PictureSelectorTool {

    /*fun PictureMediaType(act: Activity, list: List<LocalMedia>, position: Int) {
       if (list.size <= 0) return
       val media = list[position]
       val pictureType = media.pictureType
       val mediaType = PictureMimeType.pictureToVideo(pictureType)
       when (mediaType) {
           1 ->
               // 预览图片 可自定长按保存路径
               //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
               PictureSelector.create(act).themeStyle(R.style.picture_default_style).openExternalPreview(
                   position,
                   list
               )
           2 ->
               // 预览视频
               PictureSelector.create(act).externalPictureVideo(media.path)
           3 ->
               // 预览音频
               PictureSelector.create(act).externalPictureAudio(media.path)
       }
   }

   fun PictureSelectorImage(act: Activity, selectionMedi: List<LocalMedia>, requst: Int) {
       PictureSelectorImage(act, selectionMedi, 9, requst, false, false)
   }


   fun PictureSelectorImage(act: Activity, selectionMedi: List<LocalMedia>, number: Int, requst: Int) {
       PictureSelectorImage(act, selectionMedi, number, requst, true, true)
   }

   public fun PictureSelectorImage(
       act: Activity,
       selectionMedi: List<LocalMedia>,
       number: Int,
       requst: Int,
       enableCrop: Boolean,
       circleDimmedLayer: Boolean
   ) {
       PictureSelector.create(act)
           .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
           .maxSelectNum(number)// 最大图片选择数量 int
           .minSelectNum(1)// 最小选择数量 int
           .imageSpanCount(4)// 每行显示个数 int
           .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
           .previewImage(true)// 是否可预览图片 true or false
           .isCamera(true)// 是否显示拍照按钮 true or false
           .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
           .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
           .enableCrop(enableCrop)// 是否裁剪 true or false
           .circleDimmedLayer(false)// 是否圆形裁剪 true or false
           .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
           .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
           .compress(true)// 是否压缩 true or false
           .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
           .isGif(false)// 是否显示gif图片 true or false
           .withAspectRatio(1, 1)
           //                .compressSavePath(getPath())//压缩图片保存地址
           .openClickSound(false)// 是否开启点击声音 true or false
           .selectionMedia(selectionMedi)// 是否传入已选图片 List<LocalMedia> list
           .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
           .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
           .minimumCompressSize(150)// 小于100kb的图片不压缩
           .synOrAsy(true)//同步true或异步false 压缩 默认同步
           .forResult(requst)//结果回调onActivityResult code
   }

   fun SelectorVideo(act: Activity, request: Int) {
       // 进入相册 以下是例子：用不到的api可以不写
       PictureSelector.create(act)
           .openGallery(PictureMimeType.ofVideo())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
           .imageSpanCount(4)// 每行显示个数 int
           .isCamera(true)
           .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
           .previewVideo(true)// 是否可预览视频 true or false
           .enablePreviewAudio(false) // 是否可播放音频 true or false
           .compress(true)// 是否压缩 true or false
           .synOrAsy(true)//同步true或异步false 压缩 默认同步
           .videoQuality(1)// 视频录制质量 0 or 1 int
           .videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
           .videoMinSecond(5)// 显示多少秒以内的视频or音频也可适用 int
           .recordVideoSecond(15)//视频秒数录制 默认60s int
           .forResult(request)//结果回调onActivityResult code
   }

   */
    /**
     * 直接拍照
     * @param act
     * @param request
     *//*
    fun photo(act: Context, request: Int, circleDimmedLayer: Boolean) {
        p(act, request, circleDimmedLayer)
    }

    fun photo(act: Context, request: Int) {
        p(act, request, false)
    }

    private fun p(act: Context, request: Int, circleDimmedLayer: Boolean) {
        PictureSelector.create(act as Activity)
            .openCamera(PictureMimeType.ofImage())
            .enableCrop(true)// 是否裁剪 true or false
            .circleDimmedLayer(circleDimmedLayer)
            .compress(true)// 是否压缩 true or false
            .showCropFrame(if (circleDimmedLayer == true) false else true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
            .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
            .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
            .minimumCompressSize(150)// 小于100kb的图片不压缩
            .forResult(request)
    }*/

    //选择图片
    fun PictureSelectorImage(act: Activity, localMedia: List<LocalMedia>) {
        SelectorVideo(act, localMedia, false, SelectMimeType.ofImage())
    }

    fun SelectorVideo(act: Activity, localMedia: List<LocalMedia>, isAutoVideoPlay: Boolean, chooseMode: Int) {
        // 预览图片、视频、音频

    }

}
