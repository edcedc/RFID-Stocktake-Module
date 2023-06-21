package com.yc.reid.mvp.presenter

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ThreadUtils
import com.yc.reid.R
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
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import org.litepal.LitePal
import org.litepal.LitePal.deleteAll
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

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

        //删除当前用户的数据库
        deleteAll(StocktakeListSql::class.java, "roNo=?", roNo)
        deleteAll(StockChildSql::class.java, "roNo=?", roNo)
        deleteAll(UploadStockDataSql::class.java, "roNo=?", roNo)

        val disposable = RetrofitManager.service.stockTakeList(roNo, companyid)
            .compose(SchedulerUtils.ioToMain())
            .subscribe({ bean ->
                mRootView?.apply {
                    if (bean.code == ErrorStatus.SUCCESS) {
                        val list = bean.data
                        if (list != null && list.size != 0) {
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
                                stockTakeListAsset(dataBean, roNo, companyid, bean.count)
                                mRootView?.setProgress(bean.count, listBean.size)
                            }
                            mRootView?.setData(list as Object)
//                            mRootView?.hideLoading()
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

    fun stockTakeListAsset(dataBean: DataBean, roNo: String, companyid: String, count: Int) {
        val stocktakeno = dataBean.stocktakeno!!//父类订单ID
        val disposable = RetrofitManager.service.stockTakeListAsset(stocktakeno, roNo, companyid)
            .compose(SchedulerUtils.ioToMain())
//            .observeOn(Schedulers.io())
            .subscribe({ json ->
                mRootView?.apply {
                    val json = JSONObject(json.string())
                    if (json.optInt("code") == ErrorStatus.SUCCESS) {
                        val data = json.optJSONObject("data")
                        if (data != null) {
                            val data2 = data.optJSONArray("data2")
                            if (data2 != null && data2.length() != 0) {

                               /* Flowable.create(FlowableOnSubscribe { e: FlowableEmitter<JSONObject?>? ->
                                    for (i in 0 until data2.length()) {
                                        val opt = data2.optJSONObject(i)
                                        e?.onNext(opt)
                                    }
                                } as FlowableOnSubscribe<JSONObject?>, BackpressureStrategy.BUFFER)
                                    .observeOn(AndroidSchedulers.mainThread()) //给下面分配了UI线程
                                    .doOnNext { obj ->
                                        LogUtils.e("是你吗" + obj)
                                    }*/

                                /*Flowable.create(FlowableOnSubscribe { e: FlowableEmitter<JSONObject?>? ->
                                    for (i in 0 until data2.length()) {
                                        val opt = data2.optJSONObject(i)
                                        e?.onNext(opt)
                                    }
                                } as FlowableOnSubscribe<JSONObject?>,
                                    BackpressureStrategy.BUFFER
                                )
                                    .observeOn(Schedulers.io())
                                    .map(object : Function)*/
                                   /* .subscribe(object : Subscriber<JSONObject?> {
                                           override fun onSubscribe(s: Subscription) {
                                               s.request(10);
                                           }

                                        override fun onNext(integer: JSONObject?) {

                                        }

                                        override fun onError(t: Throwable) {

                                        }

                                        override fun onComplete() {

                                        }
                                    })*/


                                val jsonList = ArrayList<JSONObject>()
                                for (i in 0 until data2.length()) {
                                    val opt = data2.optJSONObject(i)
                                    jsonList.add(opt)
                                }

                                val chunkedNumbers = jsonList.chunked(2)

                                chunkedNumbers.forEach() { array ->

                                    ThreadUtils.executeByCpu(object : ThreadUtils.Task<Any?>() {
                                        @Throws(Throwable::class)
                                        override fun doInBackground(): Any? {

                                            for (i in 0 until array.size) {
                                                val opt = array[i]
                                                val bean = StockChildSql()
                                                bean.ids = stocktakeno + opt.optString("AssetNo")
                                                bean.OrderNo = stocktakeno
                                                bean.AssetNo = opt.optString("AssetNo")
                                                bean.LabelName = opt.optString("LabelName")
                                                bean.LabelTag = opt.optString("LabelTag")
                                                bean.CreateUser = opt.optString("CreateUser")
                                                bean.CreateDate = opt.optString("CreateDate")
                                                bean.StatusName = opt.optString("StatusName")
                                                bean.remarks = opt.optString("remarks")
                                                bean.roNo = roNo
                                                bean.companyid = companyid

                                                //获取用户信息
                                                val Inventory = opt.optString("Inventory")
                                                if (!StringUtils.isEmpty(Inventory)) {
                                                    bean.Inventory = opt.optString("Inventory")
                                                    val inventoryObj = JSONObject(Inventory)
                                                    bean.scan_status = inventoryObj.optInt("FoundStatus")
                                                    bean.type = inventoryObj.optInt("StatusID")
                                                }

                                                //存默认的数据
                                                var list = JSONArray()

                                                //手动添加Tag数据
                                                var tagObj = JSONObject()
                                                tagObj.put("title", act?.getString(R.string.asset_material))
                                                val tag = opt.optString("Tag")
                                                if (!StringUtils.isEmpty(tag)) {
                                                    bean.Tag = opt.optString("Tag")
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
                                                val inventory = opt.optString("Inventory")
                                                if (!StringUtils.isEmpty(inventory)) {
                                                    val obj = JSONObject(tag)
                                                    var array = JSONArray()
                                                    obj.put(
                                                        act?.getString(R.string.label),
                                                        bean.LabelTag
                                                    )
                                                    val headerkeys: Iterator<String> = obj.keys()
                                                    while (headerkeys.hasNext()) {
                                                        val headerkey = headerkeys.next()
                                                        val headerValue: String =
                                                            obj.getString(headerkey)
                                                        var inventoryObt = JSONObject()
                                                        inventoryObt.put("title", headerkey)
                                                        inventoryObt.put("text", headerValue)
                                                        array.put(inventoryObt)
                                                    }
                                                    inventoryObj.put("list", array)
                                                    list.put(inventoryObj)
                                                }

                                                //org.json.JSONObject解析方法
                                                val headerkeys: Iterator<String> = data.keys()
                                                while (headerkeys.hasNext()) {
                                                    val headerkey = headerkeys.next()
                                                    val headerValue: String =
                                                        data.getString(headerkey)
                                                    var jsonObject = JSONObject()
                                                    jsonObject.put("title", headerkey)
                                                    if (headerkey.equals("data2")) {
                                                        break
                                                    }
                                                    var jsonArray2 = JSONArray()
                                                    var jsonObject2 = JSONObject(headerValue)
                                                    val headerkeys2: Iterator<String> =
                                                        jsonObject2.keys()
                                                    while (headerkeys2.hasNext()) {
                                                        val headerkey2 = headerkeys2.next()
                                                        val headerValue2: String =
                                                            jsonObject2.getString(headerkey2)
                                                        var jsonObject3 = JSONObject()
                                                        jsonObject3.put("title", headerkey2)

                                                        //存第二层的数据
                                                        val dataHeaderkeys: Iterator<String> =
                                                            opt.keys()
                                                        while (dataHeaderkeys.hasNext()) {
                                                            val dataHeaderkey =
                                                                dataHeaderkeys.next()
                                                            val dataHeaderValue: String =
                                                                opt.getString(dataHeaderkey)
                                                            if (headerkey2.equals(dataHeaderkey)) {
                                                                jsonObject3.put(
                                                                    "text",
                                                                    dataHeaderValue
                                                                )
                                                            }
                                                        }
                                                        jsonArray2.put(jsonObject3)
                                                        //都存起来
                                                        jsonObject.put("list", jsonArray2)
                                                    }
                                                    list.put(jsonObject)

                                                    bean.data = list.toString()
                                                    bean.save()

//                                            listBean.add("")
                                                }
                                            }
                                            return null
                                        }

                                        override fun onSuccess(result: Any?) {
//                                            mRootView?.setProgress(count, listBean.size)
                                            LogUtils.e("onSuccess")
                                        }

                                        override fun onCancel() {
                                            LogUtils.e("onCancel")
                                        }

                                        override fun onFail(t: Throwable) {
                                            LogUtils.e(t.message)
                                        }
                                    })
                                }
                            }

//                            mRootView?.setProgress(count, listBean.size)
//                                 val stocktakeListSql = LitePal.where("stocktakeno = ?", stocktakeno).findFirst(StocktakeListSql::class.java)
//                                 stocktakeListSql.jsonObject = data.toString()
//                                 stocktakeListSql.save()
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