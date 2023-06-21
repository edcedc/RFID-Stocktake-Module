package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import com.yc.reid.R
import com.yc.reid.base.BaseFragment
import com.yc.reid.api.UIHelper
import com.yc.reid.mvp.impl.OneContract
import com.yc.reid.mvp.presenter.OnePresenter
import kotlinx.android.synthetic.main.f_one.*

/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/10/23
 * Time: 18:19
 */
class OneFrg: BaseFragment(), OneContract.View{

    val mPresenter by lazy { OnePresenter() }

    override fun getLayoutId(): Int = R.layout.f_one

    override fun initParms(bundle: Bundle) {
    }

    override fun initView(rootView: View) {
        mPresenter.init(this, activity)
        bt_play.setOnClickListener {
//            UIHelper.startVideoAct("https://memashejiao.oss-cn-shenzhen.aliyuncs.com/4dea6b53fe7c784f2049542c6d151869.mp4", "https://memashejiao.oss-cn-shenzhen.aliyuncs.com/4dea6b53fe7c784f2049542c6d151869.mp4")
            UIHelper.startHtmlAct(2, "http://47.243.120.137/StandardAMS_AMSWebService_DBSchenker/MobileWebService.asmx/assetsDetail?userid=CDA015922BC44391AA00C9AF8C2DF768&companyid=dbs&assetno=&lastcalldate=")
        }
        btn_commit.setOnClickListener { mPresenter.onRequest(1) }
    }

    override fun setRefreshLayoutMode(totalRow: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setData(objects: Object) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
