package com.yc.reid.mvp.impl

import com.yc.reid.base.IBaseListView
import com.yc.reid.base.IListPresenter
import com.yc.reid.bean.sql.UploadStockDataSql

/**
 * @Author nike
 * @Date 2023/6/13 14:57
 * @Description
 */
interface UploadContract {

    interface View : IBaseListView {
        fun onSuccess(position: Int)
    }

    interface Presenter: IListPresenter<View> {

        fun onRequest(page: Int)
        fun onUpload(position: Int, bean: UploadStockDataSql)
        fun onUploadImage(bean: UploadStockDataSql)

    }

}