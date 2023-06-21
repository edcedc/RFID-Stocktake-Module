package com.yc.reid.mvp.impl

import com.yc.reid.base.IBaseListView
import com.yc.reid.base.IListPresenter

/**
 * @Author nike
 * @Date 2023/6/14 17:49
 * @Description
 */
interface DownloadContract {

    interface View : IBaseListView {
        fun setProgress(count: Int, size: Int)
        fun setUploadHiht()
    }

    interface Presenter: IListPresenter<View> {

        fun onRequest(page: Int)

    }

}