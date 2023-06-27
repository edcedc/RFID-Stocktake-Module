package com.yc.reid.mvp.presenter

import android.os.Handler
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.yc.reid.INVENTORY_READ
import com.yc.reid.R
import com.yc.reid.base.BaseListPresenter
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.bean.sql.StocktakeListSql
import com.yc.reid.bean.sql.UploadStockDataSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.mvp.impl.InventoryTwoContract
import org.json.JSONArray
import org.json.JSONObject
import org.litepal.LitePal

/**
 * @Author nike
 * @Date 2023/5/31 10:38
 * @Description
 */
class InventoryTwoPresenter : BaseListPresenter<InventoryTwoContract.View>(), InventoryTwoContract.Presenter{

    override fun onRequest(page: Int) {}

    override fun onRequest(page: Int, orderId: String) {
        if (StringUtils.isEmpty(orderId))return
        val listBean = ArrayList<StockChildSql>()
        //根据一级清单ID和公司ID匹配
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
        }
        mRootView?.setData(listBean, JSONObject())
        mRootView?.hideLoading()
    }

    override fun onChildRequest(stocktakeno: String?, type: Int) {
        if (type != -1){
            val stockChildSqlList = LitePal.where("OrderNo = ? and type = ?", stocktakeno, type.toString()).find(StockChildSql::class.java)
            mRootView?.setData(stockChildSqlList as Object)
        }else{
            val stockChildSqlList = LitePal.where("OrderNo = ? and type != 3", stocktakeno).find(StockChildSql::class.java)
            if (stockChildSqlList != null && stockChildSqlList.size != 0){
                mRootView?.setData(stockChildSqlList as Object)
            }
        }
    }

    override fun onUpload(stocktakeno: String?) {
        val stockChildSqlList = LitePal.where("OrderNo = ? and type = 1 or type = 3", stocktakeno).find(StockChildSql::class.java)
        if (stockChildSqlList == null || stockChildSqlList.size == 0){
            showToast(act!!.getString(R.string.no_found1))
            return
        }
        mRootView?.showLoading()
        var uploadStockDataSql = UploadStockDataSql()
        uploadStockDataSql.roNo = LitePal.findFirst(UserDataSql::class.java).RoNo
        uploadStockDataSql.orderNo = stocktakeno
        uploadStockDataSql.isSave = 0
        uploadStockDataSql.time = TimeUtils.getNowString()
        val loginId = LitePal.findFirst(UserDataSql::class.java).LoginID
        val companyid = LitePal.findFirst(ConfigDataSql::class.java).companyid
        uploadStockDataSql.userid = loginId
        uploadStockDataSql.companyid = companyid

        var jsonArray = JSONArray()
        stockChildSqlList.forEachIndexed() { i, bean ->
            val jsonObject = JSONObject()
            jsonObject.put("loginID", loginId)
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
        uploadStockDataSql.save()
        //加载卡顿，延迟一下
        Handler().postDelayed({
            mRootView?.hideLoading()
            showToast(act!!.getString(R.string.saved_successfully))
        }, 1000)
    }
}