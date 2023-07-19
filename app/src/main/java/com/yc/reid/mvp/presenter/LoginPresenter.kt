package com.yc.reid.mvp.presenter

import android.content.Context
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.yc.reid.R
import com.yc.reid.api.CloudApi
import com.yc.reid.api.UIHelper
import com.yc.reid.base.BasePresenter
import com.yc.reid.bean.DataBean
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.bean.sql.UserListSql
import com.yc.reid.mar.MyApplication
import com.yc.reid.mvp.impl.LoginContract
import com.yc.reid.net.RetrofitManager
import com.yc.reid.net.exception.ErrorStatus
import com.yc.reid.net.exception.ExceptionHandle
import com.yc.reid.net.exception.SchedulerUtils
import org.litepal.LitePal
import org.litepal.LitePal.findFirst
import java.io.FileOutputStream


/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/9/19
 * Time: 17:05
 */
class LoginPresenter : BasePresenter<LoginContract.View>(),LoginContract.Presenter{

    override fun onLogin(text : String, pwd : String) {
        var companyID: String = "RFIDInventory"
        val bean = findFirst(ConfigDataSql::class.java)
        if (bean != null){
            companyID = bean.companyid!!
        }
        val ping = NetworkUtils.isConnected()
        if (!ping){
            val findFirst = LitePal.where("LoginID = ? and Password = ?", text, pwd).findFirst(UserListSql::class.java)
            if (findFirst != null){
                setUserLogin(text, pwd, companyID)
            }else{
                showToast("Please operate under good network conditions")
            }
            return
        }
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(pwd)){
            showToast(act!!.getString(R.string.error_))
            return
        }
        mRootView?.showLoading()
        val disposable = RetrofitManager.service.CheckLogin(companyID, text, pwd)
            .compose(SchedulerUtils.ioToMain())
            .subscribe({
                    bean ->
                mRootView?.apply {
                    if (bean.code == ErrorStatus.SUCCESS){
                        val data = bean.data
                        if (data != null){
                            setUserLogin(text, pwd, data.RoNo!!)

                            userList(companyID)
                        }
                        when(bean.msg){
                            104, 103 ->showToast(act!!.getString(R.string.error_phone))
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
        addSubscription(disposable)

        /*val disposable = RetrofitManager.service.getFirstHomeData()
            .compose(SchedulerUtils.ioToMain())
            .subscribe({ bean ->
                mRootView?.apply {

                    Log.e("xxx", "进来了" + bean.get(0).id + "???")
                }
            }, { t ->
                mRootView?.apply {
                    //处理异常
                    mRootView?.errorText(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }
            })
        addSubscription(disposable)*/

        /*   val create = RequestBody.create(MediaType.parse("multipart/form-data"), File(localMediaList[0].path))
             val imgBody = MultipartBody.Part.createFormData("image", "不知传什么",  create)

              val map = HashMap<String, RequestBody>()
              for (i in localMediaList.indices) {
                  val create = RequestBody.create(MediaType.parse("multipart/form-data"), File(localMediaList[i].path))
                  map.put("file" + i, create)
              }*/

    }

    private fun setUserLogin(LoginID: String, pwd: String, RoNo: String) {
        var bean = findFirst(UserDataSql::class.java)
        if (bean == null) {
            bean = UserDataSql()
        }
        bean.LoginID = LoginID
        bean.Password = pwd
        //                            bean.Phone = "456"
        bean.RoNo = RoNo
        bean.save()

        UIHelper.startMainAct()
        ActivityUtils.finishAllActivities()
    }

    override fun initApi() {
        val bean = findFirst(ConfigDataSql::class.java)
        if (bean != null){
            CloudApi.SERVLET_URL = bean.url.toString()
        }
    }

    override fun userList(companyID: String) {
        val disposable = RetrofitManager.service.userList(companyID)
            .compose(SchedulerUtils.ioToMain())
            .subscribe({
                    bean ->
                mRootView?.apply {
                    if (bean.code == ErrorStatus.SUCCESS){
                        val list = bean.data
                        if (list != null && list.size != 0){
                            LitePal.deleteAll(UserListSql::class.java)
                            list!!.forEachIndexed(){index, dataBean ->
                                var userList = UserListSql()
                                userList.LoginID = dataBean.LoginID
                                userList.Password = dataBean.Password
                                userList.RoNo = dataBean.RoNo
                                userList.save()
                            }
                        }
                    }
                }
            },{ t ->
                mRootView?.apply {
                    //处理异常
                    mRootView?.errorText(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }

            })
        addSubscription(disposable)
    }

    fun saveToRom(content: String) {
        try {
            val fos: FileOutputStream = MyApplication.mContext.openFileOutput("tel.txt", Context.MODE_PRIVATE)
            fos.write(content.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            LogUtils.e(e)
            e.printStackTrace()
        }
    }


}