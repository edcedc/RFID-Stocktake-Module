package com.yc.reid.ui.act

import android.os.Bundle
import com.yc.reid.R
import com.yc.reid.base.BaseActivity
import com.yc.reid.mvp.impl.LoginContract
import com.yc.reid.mvp.presenter.LoginPresenter
import java.io.File

/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2020/7/2
 * Time: 11:55
 */
class CeShiAct : BaseActivity(), LoginContract.View{

    val mPresenter by lazy { LoginPresenter() }


    override fun getLayoutId(): Int = R.layout.b_title_recycler

    override fun initView() {
        mPresenter.init(this)

        mPresenter.onLogin("", "")
    }

    override fun initParms(bundle: Bundle) {
    }



}