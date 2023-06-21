package com.yc.reid.mvp.impl

import com.yc.reid.base.IBaseListView
import com.yc.reid.base.IListPresenter

/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/9/25
 * Time: 11:46
 */
interface OneContract {

    interface View : IBaseListView {

    }

    interface Presenter: IListPresenter<View> {

        fun onRequest(page: Int)

    }

}