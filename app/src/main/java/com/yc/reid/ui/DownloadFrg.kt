package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.blankj.utilcode.util.LogUtils
import com.yc.reid.R
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.StocktakeListSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.event.DownloadStateEvent
import com.yc.reid.mvp.impl.DownloadContract
import com.yc.reid.mvp.presenter.DownloadPresenter
import kotlinx.android.synthetic.main.f_download.tv_text
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import org.litepal.LitePal.findFirst

/**
 * @Author nike
 * @Date 2023/6/13 10:30
 * @Description 上传
 */
class DownloadFrg : BaseFragment(), DownloadContract.View, OnClickListener {

    val mPresenter by lazy { DownloadPresenter() }

    override fun getLayoutId(): Int = R.layout.f_download

    override fun initParms(bundle: Bundle) {}

    override fun initView(rootView: View) {
        setTitle(getString(R.string.download))
        mPresenter.init(this)
//        showUiLoading()
        mPresenter.onRequest(pagerNumber)
        tv_text.setOnClickListener(this)
    }

    override fun setRefreshLayoutMode(totalRow: Int) {}

    override fun setData(objects: Object) {
        tv_text.text = getText(R.string.download_complete)
        EventBus.getDefault().post(DownloadStateEvent(true))
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_text ->{
                val userDataSql = findFirst(UserDataSql::class.java)
                val configDataSql = findFirst(ConfigDataSql::class.java)
                val stocktakeListSql = LitePal.where("roNo = ? and companyid = ?", userDataSql.RoNo, configDataSql.companyid).find(StocktakeListSql::class.java)
                LogUtils.e(stocktakeListSql.size)
            }
        }
    }

}