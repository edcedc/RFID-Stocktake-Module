package com.yc.reid.mvp.impl

import com.yc.reid.base.IBaseListView
import com.yc.reid.base.IListPresenter

/**
 * @Author nike
 * @Date 2023/5/31 10:37
 * @Description
 */
interface InventoryDetailsContract {

    interface View : IBaseListView {
        fun showLoading1()
    }

    interface Presenter: IListPresenter<View> {

        fun onRequest(page: Int)

        fun onRequest(page: Int, orderId: String)

        fun onChildRequest(stocktakeno: String?, MAIN_FIRST: Int)
        fun onUpload(stocktakeno: String?)

    }

}