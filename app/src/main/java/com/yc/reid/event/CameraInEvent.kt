package com.yc.reid.event

/**
 * 作者：yc on 2018/7/25.
 * 邮箱：501807647@qq.com
 * 版本：v1.0
 */

class CameraInEvent(val requestCode: Int,  val request: Int, val `object`: Any) {

    companion object {
        val HEAD_CAMEAR = 0//头像_相册
        val HEAD_PHOTO = 1//头像_相机
    }
}
