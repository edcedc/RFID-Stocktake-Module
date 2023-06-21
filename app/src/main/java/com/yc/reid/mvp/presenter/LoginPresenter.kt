package com.yc.reid.mvp.presenter

import android.content.Context
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.yc.reid.R
import com.yc.reid.api.CloudApi
import com.yc.reid.api.UIHelper
import com.yc.reid.base.BasePresenter
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.mar.MyApplication
import com.yc.reid.mvp.impl.LoginContract
import com.yc.reid.net.RetrofitManager
import com.yc.reid.net.exception.ErrorStatus
import com.yc.reid.net.exception.ExceptionHandle
import com.yc.reid.net.exception.SchedulerUtils
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
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(pwd)){
            showToast(act!!.getString(R.string.error_))
            return
        }
        mRootView?.showLoading()
        var companyID: String = "RFIDInventory"//初始化ID
        val bean = findFirst(ConfigDataSql::class.java)
        if (bean != null){
            companyID = bean.companyid!!
        }
        val disposable = RetrofitManager.service.CheckLogin(companyID, text, pwd)
            .compose(SchedulerUtils.ioToMain())
            .subscribe({
                    bean ->
                mRootView?.apply {
                    if (bean.code == ErrorStatus.SUCCESS){
                        val data = bean.data
                        if (data != null){
                            var bean = findFirst(UserDataSql::class.java)
                            if (bean == null){
                                bean = UserDataSql()
                            }
                            bean.LoginID = text
                            bean.Password = pwd
                            bean.RoNo = data.RoNo
                            bean.save()

                            UIHelper.startMainAct()
                            ActivityUtils.finishAllActivities()
                        }
                        when(bean.msg){
                            104 ->showToast(act!!.getString(R.string.error_phone))
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

    override fun initApi() {
        val bean = findFirst(ConfigDataSql::class.java)
        if (bean != null){
            CloudApi.SERVLET_URL = bean.url.toString()
        }
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