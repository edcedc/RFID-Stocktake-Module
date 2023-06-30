package com.yc.reid.mvp.presenter

import android.os.Handler
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.yc.reid.INVENTORY_READ
import com.yc.reid.R
import com.yc.reid.base.BaseListPresenter
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.bean.sql.UploadStockDataSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.mvp.impl.InventoryDetailsContract
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import org.litepal.LitePal

/**
 * @Author nike
 * @Date 2023/5/31 10:38
 * @Description
 */
class InventoryDetailsPresenter : BaseListPresenter<InventoryDetailsContract.View>(), InventoryDetailsContract.Presenter{

    override fun onRequest(page: Int) {}

    override fun onRequest(page: Int, orderId: String) {
        Flowable.fromCallable {
            if (!StringUtils.isEmpty(orderId)) {
                val stockChildList = LitePal.where("OrderNo = ? and roNo = ?", orderId, LitePal.findFirst(UserDataSql::class.java).RoNo).find(StockChildSql::class.java)
                stockChildList
            } else {
                null
            }
        }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { stockChildList ->
            if (stockChildList != null && stockChildList.size != 0) {
                mRootView?.setData(stockChildList as Object)
            } else {
                mRootView?.hideLoading()
            }
        }


        /*  //根据一级清单ID和公司ID匹配
         val stocktakeListSql = LitePal.where("stocktakeno = ?", orderId).findFirst(StocktakeListSql::class.java)
         if (stocktakeListSql != null){
             val s = stocktakeListSql.jsonObject
             if (s != null) {
                 val jsonObject = JSONObject(s)
                 val data2 = jsonObject.optJSONArray("data2")
                 if (data2 == null)return
                 for (i in 0 until data2.length()) {
                     val opt = data2.getJSONObject(i)
                     val OrderNo = opt.optString("OrderNo")
                     val AssetNo = opt.optString("AssetNo")

                     val stockChildSql = LitePal.where("OrderNo = ? and AssetNo = ?", OrderNo, AssetNo).findFirst(StockChildSql::class.java)
                     if (stockChildSql != null){
                         listBean.add(stockChildSql)
                     }else{
                         val bean = StockChildSql()
                         bean.ids = OrderNo + opt.optString("AssetNo")
                         bean.OrderNo = OrderNo
                         bean.AssetNo = opt.optString("AssetNo")
                         bean.LabelName = opt.optString("LabelName")
                         bean.LabelTag = opt.optString("LabelTag")
                         bean.CreateUser = opt.optString("CreateUser")
                         bean.CreateDate = opt.optString("CreateDate")
                         bean.StatusName = opt.optString("StatusName")
                         bean.FoundStatus = opt.optInt("FoundStatus")
                         bean.remarks = opt.optString("remarks")
                         bean.roNo = LitePal.findFirst(UserDataSql::class.java).RoNo
                         bean.companyid = LitePal.findFirst(ConfigDataSql::class.java).companyid
                         bean.data = jsonObject.toString()
                         bean.Tag = opt.optString("Tag")
                         bean.save()
                         listBean.add(bean)
                     }
                 }
             }
         }*/
    }

    fun queryStockChildList(stocktakeno: String, type: Int): Flowable<List<StockChildSql>> {
        return Flowable.defer {
            val queryFlowable = if (type != -1) {
                Flowable.fromCallable {
                    LitePal.where("OrderNo = ? and type = ?", stocktakeno, type.toString()).find(StockChildSql::class.java)
                }
            } else {
                Flowable.fromCallable {
                    LitePal.where("OrderNo = ? and type != ?", stocktakeno, "3") .find(StockChildSql::class.java)
                }
            }
            queryFlowable
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())



    }

    override fun onChildRequest(stocktakeno: String?, type: Int) {
        Flowable.fromCallable {
            if (type != -1){
                val stockChildSqls = LitePal.where("OrderNo = ? and type = ?", stocktakeno, type.toString())
                    .find(StockChildSql::class.java)
                stockChildSqls
            }else{
                val stockChildSqlList = LitePal.where("OrderNo = ? and type != 3", stocktakeno)
                    .find(StockChildSql::class.java)
                stockChildSqlList
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { stockChildList ->
                mRootView?.setData(stockChildList as Object)
                mRootView?.hideLoading()
            }

//        mRootView?.hideLoading()
    }

    override fun onUpload(stocktakeno: String?) {
        val stockChildSqlList = LitePal.where("OrderNo = ? and type = 1 or type = 3", stocktakeno).find(StockChildSql::class.java)
        if (stockChildSqlList == null || stockChildSqlList.size == 0){
            showToast(act!!.getString(R.string.no_found1))
            return
        }
        mRootView?.showLoading1()
        val userDataSql = LitePal.findFirst(UserDataSql::class.java)
        val configDataSql = LitePal.findFirst(ConfigDataSql::class.java)

        Flowable.fromCallable {
            var uploadStockDataSql = LitePal.where("orderNo = ? and roNo = ?", stocktakeno, userDataSql.RoNo).findFirst(UploadStockDataSql::class.java)
            if (uploadStockDataSql == null){
                uploadStockDataSql = UploadStockDataSql()
            }
            uploadStockDataSql.roNo = userDataSql.RoNo
            uploadStockDataSql.orderNo = stocktakeno
            uploadStockDataSql.isSave = 0
            uploadStockDataSql.time = TimeUtils.getNowString()
            uploadStockDataSql.userid = userDataSql.LoginID
            uploadStockDataSql.companyid =  configDataSql.companyid

            var jsonArray = JSONArray()
            stockChildSqlList.forEachIndexed() { i, bean ->
                val jsonObject = JSONObject()
                jsonObject.put("loginID", userDataSql.LoginID)
                jsonObject.put("orderNo", stocktakeno)
                jsonObject.put("AssetNo", bean.AssetNo)
                jsonObject.put("ScanDate", bean.scan_time)
                jsonObject.put("EPC", bean.LabelTag)
                jsonObject.put("Remarks", bean.remarks)
                jsonObject.put("statusID", if (bean.type == INVENTORY_READ)INVENTORY_READ else 2)
                jsonObject.put("FoundStatus", bean.scan_status)
                jsonObject.put("imageList", bean.iamgeList)
                jsonArray.put(jsonObject)
            }
            uploadStockDataSql.data = jsonArray.toString()
            uploadStockDataSql
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { uploadStockDataSql ->
                uploadStockDataSql.save()
                //加载卡顿，延迟一下
                Handler().postDelayed({
                    mRootView?.hideLoading()
                    showToast(act!!.getString(R.string.saved_successfully))
                }, 600)
            }
    }
}