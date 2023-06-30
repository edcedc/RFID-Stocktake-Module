package com.yc.reid
import android.view.View

/**
 * 作者　: hegaojian
 * 时间　: 2020/4/16
 * 描述　:
 */

/**
 * 进入订单详情item的不同页面判断值
 */
var ORDER_DESC = 1
var ORDER_CREATE = 0

/**
 *  首页frg index 号
 */
 val MAIN_FIRST : Int = 0
 val MAIN_SECOND : Int = 1
 val MAIN_THIRD : Int = 2
 val MAIN_FOUR : Int = 3

/**
 *  盘点清单二级状态
 */
 val INVENTORY_ALL : Int = 0//全部
 val INVENTORY_READ : Int = 1//在库
 val INVENTORY_NOT : Int = 2//不在库
 val INVENTORY_FAIL : Int = 3//异常

/**
 *  订单状态值
 */
val STATE_0 : Int = 0
val STATE_1 : Int = 1
val STATE_2 : Int = 2

/**
 *  连接状态
 */
val CONNECTING: Int = 0
val CONNECTION_SUCCEEDED: Int = 1
val CONNECTION_FAILED: Int = 2

/**
 *  跟后台协议上传图片用stringbuffer ? 隔开
 */
val UPLOAD_IMAGE_SPLIT: String = "?"

/**
 *  扫描类型
 */
val SCAN_STATUS_SCAN: Int = 116
val SCAN_STATUS_QRCODE: Int = 117
val SCAN_STATUS_MANUALLY: Int = 118
