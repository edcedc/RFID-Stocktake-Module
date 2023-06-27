package com.yc.tea.api

import com.blankj.utilcode.util.TimeUtils
import com.yc.reid.bean.BaseListBean
import com.yc.reid.bean.BaseResponseBean
import com.yc.reid.bean.DataBean
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.UserDataSql
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.litepal.LitePal
import retrofit2.http.*


/**
 * Created by xuhao on 2017/11/16.
 * Api 接口
 */

interface ApiService{

    //登录
    @GET("CheckLogin")
    fun CheckLogin(@Query("companyID") companyID : String, @Query("loginID") loginID : String, @Query("userPwd") userPwd : String): Observable<BaseResponseBean<DataBean>>


    //获取用户账号密码
    @GET("userList")
    fun userList(@Query("companyID") companyID : String): Observable<BaseListBean<DataBean>>

    //天行数据转换详情链接  今日头条
    @POST
    fun htmltextIndex(
        @Url url: String = "assetsDetail"
    ): Observable<Object>

    @GET("assetsList")
    fun getFirstHomeData(@Query("userid") num:String?= "CDA015922BC44391AA00C9AF8C2DF768", @Query("companyid") companyid:String? = "dbs"
                         , @Query("assetno") assetno:String?= "", @Query("lastcalldate") lastcalldate:String?= ""):Observable<List<DataBean>>

    //盘点列表
    @GET("stockTakeList")
    fun stockTakeList(
        @Query("userid") userid: String,
        @Query("companyid") companyid: String):
            Observable<BaseListBean<DataBean>>

    //盘点二级列表
    @GET("stockTakeListAsset")
    fun stockTakeListAsset(@Query("orderno") orderno: String,
                           @Query("userid") userid: String,
                           @Query("companyid") companyid: String,
                           @Query("time") time: Long = TimeUtils.getNowMills()):
            Observable<ResponseBody>

    //上传盘点资料
    @GET("UploadStockTake")
    fun UploadStockTake(@Query("companyID") companyID: String? = null,
                        @Query("strJson") strJson: String? = null):
            Observable<BaseResponseBean<DataBean>>

    @Multipart
    @POST("UploadImage")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun UploadImage(
        @Part("companyID") strimg: String,
        @Part("json") json: String
    ):  Observable<BaseResponseBean<DataBean>>

    @Multipart
    @POST("UploadImage")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun UploadImage(
        @PartMap map : HashMap<String, RequestBody>
    ): @JvmSuppressWildcards Observable<BaseResponseBean<DataBean>>


    @POST("UploadImage")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun userUpdate(
        @Query("companyID") num: String,
        @Body  head : MultipartBody.Part):
            Observable<BaseResponseBean<DataBean>>


}