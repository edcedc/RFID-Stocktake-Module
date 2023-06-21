package com.yc.reid.mvp.presenter

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ThreadUtils
import com.yc.reid.UPLOAD_IMAGE_SPLIT
import com.yc.reid.api.CloudApi
import com.yc.reid.base.BaseListPresenter
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.UploadStockDataSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.mvp.impl.UploadContract
import com.yc.reid.utils.FileUtils
import com.yc.reid.utils.ImageUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.litepal.LitePal
import java.net.URLEncoder

/**
 * @Author nike
 * @Date 2023/6/13 14:58
 * @Description
 */
class UploadPresenter  : BaseListPresenter<UploadContract.View>(), UploadContract.Presenter{

    override fun onRequest(page: Int) {
        val loginId = LitePal.findFirst(UserDataSql::class.java).LoginID
        val companyid = LitePal.findFirst(ConfigDataSql::class.java).companyid
        val uploadStockDataSql = LitePal.where("userid = ? and companyid = ?", loginId, companyid).find(UploadStockDataSql::class.java)
        if (uploadStockDataSql != null && uploadStockDataSql.size != 0){
            mRootView?.setData(uploadStockDataSql as Object)
        }
    }

    override fun onUpload(position: Int, beanSql: UploadStockDataSql) {
        val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val client = OkHttpClient().newBuilder().build()
        val body = ("companyID=" + beanSql.companyid + "&strJson=" + beanSql.data).toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(CloudApi.SERVLET_URL + "UploadStockTake")
            .method("POST", body)
            .addHeader("Content-Type", mediaType.toString())
            .build()
        ThreadUtils.executeByCpu(object : ThreadUtils.Task<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {
                val response: Response = client.newCall(request).execute()
                LogUtils.e("companyID=" + beanSql.companyid + "&strJson=" + beanSql.data)
                return null
            }

            override fun onSuccess(result: Any?) {
                mRootView?.onSuccess(position)
                FileUtils.writeTxtToFile("companyID=" + beanSql.companyid + "&strJson=" + beanSql.data)
                LogUtils.e(result.toString())
            }
            override fun onCancel() {
                LogUtils.e("onCancel")
            }
            override fun onFail(t: Throwable) {
                LogUtils.e(t.message)
            }
        })

        /*val disposable = RetrofitManager.service.UploadStockTake(beanSql.companyid, beanSql.data)
            .compose(SchedulerUtils.ioToMain())
            .subscribe({bean ->
                mRootView?.apply {
                    if (bean.code == ErrorStatus.SUCCESS){
                        val data = bean.data
                        if (data != null){

                        }
                    }
                    mRootView?.hideLoading()
                }
            },{ t ->
                mRootView?.apply {
                    //处理异常
                    mRootView?.errorText(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }

            })
        addSubscription(disposable)*/
    }

    override fun onUploadImage(beanSql: UploadStockDataSql) {
      val jsonArray = JSONArray(beanSql.data)

        if (jsonArray != null && jsonArray.length() != 0){
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.optJSONObject(i)
                val imageList = obj.optString("imageList")
                if (!StringUtils.isEmpty(imageList)){
                    uploadImage(beanSql.companyid, imageList, obj)
                }
            }
        }

    }

    private fun uploadImage(companyid: String?, imageList: String, obj: JSONObject?) {
        val split = imageList.split(UPLOAD_IMAGE_SPLIT)
        val sb = StringBuffer()
        split.forEachIndexed(){index, s ->
            if (!StringUtils.isEmpty(s)){
                val imageToBase64 = ImageUtils.imageToBase64(s)
                sb.append(imageToBase64).append(UPLOAD_IMAGE_SPLIT)
            }
        }
        FileUtils.writeTxtToFile(sb.toString());
        val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val client = OkHttpClient().newBuilder().build()
        val body = ("companyID=" + companyid + "&strJson=" + URLEncoder.encode(sb.toString(), "UTF-8") + "&loginID=" + obj!!.optString("LoginID")+
                "&orderNo=" + obj.optString("orderNo") + "&AssetNo=" + obj.optString("AssetNo"))
            .toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(CloudApi.SERVLET_URL + "FileToByte")
            .method("POST", body)
            .addHeader("Content-Type", mediaType.toString())
            .build()
        ThreadUtils.executeByCpu(object : ThreadUtils.Task<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {
                val response: Response = client.newCall(request).execute()
//                LogUtils.e("companyID=" + companyid + "&strJson=" + URLEncoder.encode(sb.toString(), "UTF-8") + "&loginID=" + obj!!.optString("LoginID")+
//                        "&orderNo=" + obj.optString("orderNo") + "&AssetNo=" + obj.optString("AssetNo"))
                return null
            }

            override fun onSuccess(result: Any?) {
                LogUtils.e("onSuccess")
                showToast("保存成功")
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