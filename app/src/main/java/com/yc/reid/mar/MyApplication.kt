package com.yc.reid.mar

import android.app.Application
import android.content.Context
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.yc.reid.service.InitializeService
import kotlin.properties.Delegates
import com.tencent.bugly.crashreport.CrashReport
import com.yc.reid.R
import com.yc.reid.utils.LogcatHelper
import org.litepal.LitePal

/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/9/20
 * Time: 15:33
 */
class MyApplication : Application(){

    companion object {

        var mContext: Context by Delegates.notNull()
            private set

        init {
            //设置全局默认配置（优先级最低，会被其他设置覆盖）
            SmartRefreshLayout.setDefaultRefreshInitializer { context, layout ->
                //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
                layout.setReboundDuration(1000)
                layout.setFooterHeight(100f)
                layout.setDisableContentWhenLoading(false)
                layout.setPrimaryColorsId(R.color.red_FF5A60, android.R.color.white)
                layout.setDisableContentWhenRefresh(true);//是否在刷新的时候禁止列表的操作
                layout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        mContext = this;
        LogcatHelper.getInstance(this).init(this)
        LogcatHelper.getInstance(this).start()
        InitializeService.start(this)
        CrashReport.initCrashReport(applicationContext, "5cd0cff6d3", true)
        LitePal.initialize(this);
    }

}
