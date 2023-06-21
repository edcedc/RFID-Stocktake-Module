package com.yc.reid.mvp.impl

import com.yc.reid.base.IBaseListView
import com.yc.reid.base.IListPresenter
import com.yc.reid.keyctrl.entity.InventoryInfo

/**
 * @Author nike
 * @Date 2023/5/31 10:37
 * @Description
 */
interface MainContract {

    interface View : IBaseListView {
        fun setConnFail(isConn: Boolean)
        fun setData(objects: InventoryInfo?)
    }

    interface Presenter: IListPresenter<View> {

        fun onRequest(page: Int)
        fun config()
        fun conn(): Boolean
        fun initConn(): Boolean
        fun disconn()
        fun onClean()
        fun initStop()
        fun onNetwork()

    }

}