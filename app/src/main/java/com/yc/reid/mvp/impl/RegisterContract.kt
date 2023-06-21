package com.yc.reid.mvp.impl

import com.yc.reid.base.IBaseView
import com.yc.reid.base.IPresenter


/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/9/23
 * Time: 15:12
 */
interface RegisterContract {

    interface View : IBaseView {
         fun setCode()
    }

    interface Presenter: IPresenter<View> {

        fun onCode(phone : String)

        fun onSure(
            phone: String,
            code: String,
            pwd: String,
            pwd1: String,
            checked: Boolean?
        )

    }

}