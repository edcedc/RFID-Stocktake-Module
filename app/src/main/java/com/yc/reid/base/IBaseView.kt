package com.yc.reid.base

/**
 * @author Jake.Ho
 * created: 2017/10/25
 * desc:
 */
interface IBaseView {

    fun showLoading()

    fun hideLoading()

    fun errorText(text : String, errorCode : Int)

}
