package com.yc.reid.mvp.presenter

import com.yc.reid.base.BaseListPresenter
import com.yc.reid.bean.DataBean
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.bean.sql.StocktakeListSql
import com.yc.reid.bean.sql.UploadStockDataSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.mvp.impl.DownloadContract
import com.yc.reid.net.RetrofitManager
import com.yc.reid.net.exception.ErrorStatus
import com.yc.reid.net.exception.ExceptionHandle
import com.yc.reid.net.exception.SchedulerUtils
import org.json.JSONObject
import org.litepal.LitePal
import org.litepal.LitePal.deleteAll
import org.litepal.extension.delete
import org.litepal.extension.find

/**
 * @Author nike
 * @Date 2023/6/14 17:49
 * @Description
 */
class DownloadPresenter : BaseListPresenter<DownloadContract.View>(), DownloadContract.Presenter {

    override fun onRequest(page: Int) {
        val roNo = LitePal.findFirst(UserDataSql::class.java).RoNo!!
        val companyid = LitePal.findFirst(ConfigDataSql::class.java).companyid!!
        deleteAll(StocktakeListSql::class.java, "roNo=? and companyid=?", roNo, companyid)
        deleteAll(StockChildSql::class.java, "roNo=? and companyid=?", roNo, companyid)
        deleteAll(UploadStockDataSql::class.java, "roNo=? and companyid=?", roNo, companyid)
        val disposable = RetrofitManager.service.stockTakeList(roNo, companyid)
            .compose(SchedulerUtils.ioToMain())
            .subscribe({ bean ->
                mRootView?.apply {
                    if (bean.code == ErrorStatus.SUCCESS) {
                        val list = bean.data
                        if (list != null && list.size != 0) {
                            list.forEachIndexed(){index, dataBean ->
                                val stocktakeListSql = StocktakeListSql()
                                stocktakeListSql.userid = LitePal.findFirst(UserDataSql::class.java).LoginID!!
                                stocktakeListSql.roNo = roNo
                                stocktakeListSql.companyid = companyid
                                stocktakeListSql.stockId = dataBean.id.toString()
                                stocktakeListSql.stocktakeno = dataBean.stocktakeno
                                stocktakeListSql.name = dataBean.name
                                stocktakeListSql.startDate = dataBean.startDate
                                stocktakeListSql.endDate = dataBean.endDate
                                stocktakeListSql.lastUpdate = dataBean.lastUpdate
                                stocktakeListSql.total = dataBean.total
                                stocktakeListSql.progress = dataBean.progress
                                stocktakeListSql.remarks = dataBean.remarks
                                stocktakeListSql.save()
                                stockTakeListAsset(dataBean, roNo, companyid)
                            }
                            mRootView?.setData(list as Object)
                            mRootView?.hideLoading()
                        }
                    }
                }
            }, { t ->
                mRootView?.apply {
                    //处理异常
                    mRootView?.errorText(
                        ExceptionHandle.handleException(t),
                        ExceptionHandle.errorCode
                    )
                }
            })
        addSubscription(disposable)
    }

    fun stockTakeListAsset(bean: DataBean, roNo: String, companyid: String) {
        val disposable = RetrofitManager.service.stockTakeListAsset(bean.stocktakeno!!, roNo, companyid)
                .compose(SchedulerUtils.ioToMain())
                .subscribe({ json ->
                    mRootView?.apply {
                         val json = JSONObject(json.string())
                         if (json.optInt("code")== ErrorStatus.SUCCESS){
                             val data = json.optJSONObject("data")
                             if (data != null){
                             val stocktakeListSql = LitePal.where("stocktakeno = ?", bean.stocktakeno!!).findFirst(StocktakeListSql::class.java)
                                 stocktakeListSql.jsonObject = data.toString()
                                 stocktakeListSql.save()
                             }
                         }
//                    mRootView?.hideLoading()
                    }
                }, { t ->
                    mRootView?.apply {
                        //处理异常
                        mRootView?.errorText(
                            ExceptionHandle.handleException(t),
                            ExceptionHandle.errorCode
                        )
                    }
                })
        addSubscription(disposable)
    }

}