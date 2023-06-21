package com.yc.reid.base

import android.app.Activity
import androidx.fragment.app.FragmentActivity


/**
 * @author Jake.Ho
 * created: 2017/10/25
 * desc: Presenter 基类
 */


interface IListPresenter<in V: IBaseListView> {

    fun init(mRootView: V, activity: Activity?)

    fun init(mRootView: V)

    fun detachView()

}
