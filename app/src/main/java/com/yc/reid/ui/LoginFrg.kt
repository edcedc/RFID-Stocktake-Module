package com.yc.reid.ui

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.yc.reid.R
import com.yc.reid.api.CloudApi
import com.yc.reid.api.UIHelper
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.mvp.impl.LoginContract
import com.yc.reid.mvp.presenter.LoginPresenter
import kotlinx.android.synthetic.main.f_login.bt_sure
import kotlinx.android.synthetic.main.f_login.et_account
import kotlinx.android.synthetic.main.f_login.et_pwd
import kotlinx.android.synthetic.main.f_login.iv_pwd
import kotlinx.android.synthetic.main.f_login.iv_set
import org.litepal.LitePal
import org.litepal.LitePal.findFirst

/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/10/24
 * Time: 11:44
 */
class LoginFrg : BaseFragment(), LoginContract.View, OnClickListener{

    val mPresenter by lazy { LoginPresenter() }

    var isPwd = false

    override fun getLayoutId(): Int = R.layout.f_login

    override fun initParms(bundle: Bundle) {
    }

    override fun initView(rootView: View) {
        mPresenter.init(this, requireActivity())
        iv_pwd.setOnClickListener(this)
        bt_sure.setOnClickListener(this)
        iv_set.setOnClickListener(this)
        var bean = findFirst(UserDataSql::class.java)
        if (bean != null){
            et_account.setText(bean.LoginID)
            et_pwd.setText(bean.Password)
        }

        //第一次登录 设置默认参数
        var configBean = LitePal.findFirst(ConfigDataSql::class.java)
        if (configBean == null){
            configBean = ConfigDataSql()
            configBean.languagePosition = 0
            configBean.url = CloudApi.SERVLET_URL
//            configBean.companyid = "RFIDInventory"
            configBean.companyid = "im"
            configBean.save()
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.initApi()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.iv_pwd ->{
                if (!isPwd){
                    isPwd = true
                    et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                    iv_pwd.background = ContextCompat.getDrawable(requireActivity(), R.mipmap.show_password)
                }else {
                    isPwd = false
                    et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance())
                    iv_pwd.background = ContextCompat.getDrawable(requireActivity(), R.mipmap.hide_password)
                }
            }
            R.id.iv_set -> {
                UIHelper.startSetFrg(this)
            }
            R.id.bt_sure ->{
                mPresenter.onLogin(et_account.text.toString(), et_pwd.text.toString())
            }
        }
    }

}