package com.yc.reid.mvp.impl

import com.yc.reid.base.IBaseView
import com.yc.reid.base.IPresenter


/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/9/19
 * Time: 17:02
 */
interface LoginContract{

    interface View : IBaseView {

    }

    interface Presenter: IPresenter<View> {

        fun onLogin(phone : String, pwd : String)
        fun initApi()

    }

}