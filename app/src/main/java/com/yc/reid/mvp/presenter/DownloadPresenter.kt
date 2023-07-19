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
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import org.litepal.LitePal
import org.litepal.LitePal.deleteAll

/**
 * @Author nike
 * @Date 2023/6/14 17:49
 * @Description
 */
class DownloadPresenter : BaseListPresenter<DownloadContract.View>(), DownloadContract.Presenter {

    var listBean = ArrayList<String>()

    override fun onRequest(page: Int) {
        listBean.clear()
        val userDataSql = LitePal.findFirst(UserDataSql::class.java)
        val roNo = userDataSql.RoNo!!
        val LoginID = userDataSql.LoginID!!
        val companyid = LitePal.findFirst(ConfigDataSql::class.java).companyid!!

        val disposable = RetrofitManager.service.stockTakeList(roNo, companyid)
            .compose(SchedulerUtils.ioToMain())
            .subscribe({ bean ->
                mRootView?.apply {
                    if (bean.code == ErrorStatus.SUCCESS) {
                        val list = bean.data
                        if (list != null && list.size != 0) {
                            if (bean.code != 0) {
                                //删除当前用户的数据库
                                deleteAll(StocktakeListSql::class.java, "roNo=?", roNo)
                                deleteAll(StockChildSql::class.java, "roNo=?", roNo)
                                deleteAll(UploadStockDataSql::class.java, "roNo=?", roNo)
                                list.forEachIndexed() { index, dataBean ->
                                    val stocktakeListSql = StocktakeListSql()
                                    stocktakeListSql.LoginID = LoginID
                                    stocktakeListSql.roNo = roNo
                                    stocktakeListSql.companyid = companyid
                                    stocktakeListSql.ids = dataBean.id.toString()
                                    stocktakeListSql.stocktakeno = dataBean.stocktakeno
                                    stocktakeListSql.name = dataBean.name
                                    stocktakeListSql.startDate = dataBean.startDate
                                    stocktakeListSql.endDate = dataBean.endDate
                                    stocktakeListSql.lastUpdate = dataBean.lastUpdate
                                    stocktakeListSql.total = dataBean.total
                                    stocktakeListSql.progress = dataBean.progress
                                    stocktakeListSql.remarks = dataBean.remarks
                                    stocktakeListSql.save()


                                    /*Realm.getDefaultInstance().use { realm ->
                                        realm.executeTransaction { transactionRealm ->
                                            val inventorySqlOrderSql = transactionRealm.createObject(InventorySqlOrderSql::class.java)
                                            inventorySqlOrderSql.LoginID = LoginID
                                            inventorySqlOrderSql.roNo = roNo
                                            inventorySqlOrderSql.companyid = companyid
                                            inventorySqlOrderSql.ids = dataBean.id.toString()
                                            inventorySqlOrderSql.stocktakeno = dataBean.stocktakeno
                                            inventorySqlOrderSql.name = dataBean.name
                                            inventorySqlOrderSql.startDate = dataBean.startDate
                                            inventorySqlOrderSql.endDate = dataBean.endDate
                                            inventorySqlOrderSql.lastUpdate = dataBean.lastUpdate
                                            inventorySqlOrderSql.total = dataBean.total
                                            inventorySqlOrderSql.progress = dataBean.progress
                                            inventorySqlOrderSql.remarks = dataBean.remarks
                                        }
                                    }*/

                                    stockTakeListAsset(dataBean, roNo, companyid, bean.count)
                                }
                            }
                            mRootView?.setProgress(bean.count, listBean.size)
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
//        val dataBean = DataBean()
//        dataBean.stocktakeno = "0000000009"
//        stockTakeListAsset(dataBean, roNo, companyid, 1028)

    }


    fun stockTakeListAsset(dataBean: DataBean, roNo: String, companyid: String, count: Int) {
        val stocktakeno = dataBean.stocktakeno!!//父类订单ID
        val disposable = RetrofitManager.service.stockTakeListAsset(stocktakeno, roNo, companyid)
            .compose(SchedulerUtils.ioToMain())
//            .observeOn(Schedulers.io())
            .subscribe({ json ->
                mRootView?.apply {
                    val json = JSONObject(json.string())
                    if (json.optInt("code") == ErrorStatus.SUCCESS) {
                        val data = json.optJSONArray("data")
                        if (data != null && data.length() != 0) {

                            var compositeDisposable: Disposable? = null

                            val range = Flowable.range(0, data.length())

                            compositeDisposable = range
                                .onBackpressureBuffer()
                                .flatMap({ index ->
                                    val jsonObject = data.optJSONObject(index)
                                    val just = Flowable.just(jsonObject)
                                    just.flatMap({ jsonObject ->
                                        val tag = jsonObject.optJSONObject("Tag")
                                        val bean = StockChildSql()

                                        val pair = Pair(jsonObject, bean)
                                        Flowable.fromCallable {
                                            val jsonObject = pair.first as JSONObject
                                            val bean = pair.second as StockChildSql
                                            bean.ids = stocktakeno + tag.optString("AssetNo")
                                            bean.OrderNo = stocktakeno
                                            bean.AssetNo = tag.optString("AssetNo")
                                            bean.LabelTag = tag.optString("LabelTag")
                                            bean.remarks = tag.optString("remarks")
                                            bean.type = tag.optInt("InventoryStatus")
                                            bean.roNo = roNo
                                            bean.companyid = companyid

                                            bean.data = jsonObject.toString()

                                            bean
                                        }.subscribeOn(Schedulers.io())
                                    }, true, 1)
                                })
                                .subscribe { bean ->
                                    bean.save()
                                }

                            mRootView?.addCompositeDisposable(compositeDisposable)


                            /*Flowable.fromCallable {
                                val list = JSONArray()
                                val headerkeys: Iterator<String> = data.keys()
                                while (headerkeys.hasNext()) {
                                    val headerkey = headerkeys.next()
                                    val headerValue: String = data.getString(headerkey)
                                    var jsonSave = JSONObject()
                                    jsonSave.put("title", headerkey)
                                    if (headerkey.equals("data2")) {
                                        break
                                    }
                                    var jsonArray2 = JSONArray()
                                    var jsonObject2 = JSONObject(headerValue)
                                    val headerkeys2: Iterator<String> = jsonObject2.keys()
                                    while (headerkeys2.hasNext()) {
                                        val headerkey2 = headerkeys2.next()
                                        val headerValue2: String = jsonObject2.getString(headerkey2)
                                        var jsonObject3 = JSONObject()
                                        jsonObject3.put("title", headerkey2)

                                        //存第二层的数据
                                        val dataHeaderkeys: Iterator<String> = jsonSave.keys()
                                        while (dataHeaderkeys.hasNext()) {
                                            val dataHeaderkey = dataHeaderkeys.next()
                                            val dataHeaderValue: String = jsonSave.getString(dataHeaderkey)
                                            if (headerkey2.equals(dataHeaderkey)) {
                                                jsonObject3.put("text", dataHeaderValue)
                                            }
                                        }
                                        jsonArray2.put(jsonObject3)
                                        //都存起来
                                        jsonSave.put("list", jsonArray2)
                                    }
                                    list.put(jsonSave)
                                }
                                list
                            }
//                                    .observeOn(Schedulers.io())
                                .flatMap({ list ->
                                    Flowable.range(0, data2.length())
                                        .onBackpressureBuffer()
                                        .observeOn(Schedulers.io())
                                        .flatMap({ index ->
                                            val jsonObject = data2.getJSONObject(index)
                                            val bean = StockChildSql()
                                            bean.ids = stocktakeno + jsonObject.optString("AssetNo")
                                            bean.OrderNo = stocktakeno
                                            bean.AssetNo = jsonObject.optString("AssetNo")
                                            bean.LabelName = jsonObject.optString("LabelName")
                                            bean.LabelTag = jsonObject.optString("LabelTag")
                                            bean.CreateUser = jsonObject.optString("CreateUser")
                                            bean.CreateDate = jsonObject.optString("CreateDate")
                                            bean.StatusName = jsonObject.optString("StatusName")
                                            bean.remarks = jsonObject.optString("remarks")
                                            bean.roNo = roNo
                                            bean.companyid = companyid

                                            val pair = Pair(jsonObject, bean)
                                            Flowable.fromCallable {
                                                val innerJsonObject = pair.first as JSONObject
                                                val bean = pair.second as StockChildSql
                                                //手动添加Tag数据
                                                var tagObj = JSONObject()
                                                tagObj.put("title", act?.getString(R.string.asset_material))
                                                val tag = jsonObject.optString("Tag")
                                                if (!StringUtils.isEmpty(tag)) {
                                                    bean.Tag = jsonObject.optString("Tag")
                                                    val obj = JSONObject(tag)
                                                    var array = JSONArray()
                                                    obj.put(act?.getString(R.string.label), bean.LabelTag)
                                                    val headerkeys: Iterator<String> = obj.keys()
                                                    while (headerkeys.hasNext()) {
                                                        val headerkey = headerkeys.next()
                                                        val headerValue: String = obj.getString(headerkey)
                                                        var tagObt = JSONObject()
                                                        tagObt.put("title", headerkey)
                                                        tagObt.put("text", headerValue)
                                                        array.put(tagObt)
                                                    }
                                                    tagObj.put("list", array)
                                                    list.put(tagObj)
                                                }

                                                //手动添加第Inventory数据
                                                var inventoryObj = JSONObject()
                                                inventoryObj.put("title", act?.getString(R.string.inventory))
                                                val inventory = jsonObject.optString("Inventory")
                                                if (!StringUtils.isEmpty(inventory)) {
                                                    val obj = JSONObject(inventory)
                                                    var array = JSONArray()
                                                    val headerkeys: Iterator<String> = obj.keys()
                                                    while (headerkeys.hasNext()) {
                                                        val headerkey = headerkeys.next()
                                                        val headerValue: String = obj.getString(headerkey)
                                                        var inventoryObt = JSONObject()
                                                        inventoryObt.put("title", headerkey)
                                                        inventoryObt.put("text", headerValue)
                                                        array.put(inventoryObt)
                                                    }
                                                    inventoryObj.put("list", array)
                                                    list.put(inventoryObj)
                                                }else{
                                                    var array = JSONArray()
                                                    val obj = JSONObject()
                                                    obj.put("title", "InventoryStatus")
                                                    obj.put("text", INVENTORY_NOT)
                                                    array.put(obj)
                                                    val obj1 = JSONObject()
                                                    obj1.put("title", "FoundStatus")
                                                    obj1.put("text", 0)
                                                    array.put(obj1)
                                                    inventoryObj.put("list", array)
                                                    list.put(inventoryObj)
                                                }
//                                                    LogUtils.e(ThreadUtils.isMainThread())

                                                //jsonarray 直接转 string 会卡顿，需要压缩算法
                                                bean.data = list.toString()
                                                bean
                                            }
                                        }, true, 1)
                                })
                                .subscribe { bean ->
                                    bean.save()
                                }*/
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