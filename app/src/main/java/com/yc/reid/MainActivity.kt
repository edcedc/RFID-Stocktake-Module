package com.yc.reid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.navigation.NavigationView
import com.yc.reid.api.UIHelper
import com.yc.reid.base.BaseActivity
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.event.InventorySearchEvent
import com.yc.reid.event.InventorySearchIdEvent
import com.yc.reid.event.StartReceivingEvent
import com.yc.reid.event.StockTakeListEvent
import com.yc.reid.keyctrl.Common
import com.yc.reid.keyctrl.IKeyRecv
import com.yc.reid.keyctrl.KeyRecerver
import com.yc.reid.keyctrl.MyPlaySound
import com.yc.reid.keyctrl.ScanType
import com.yc.reid.keyctrl.ThreadPoolManager
import com.yc.reid.keyctrl.entity.InventoryInfo
import com.yc.reid.mvp.impl.MainContract
import com.yc.reid.mvp.presenter.MainPresenter
import com.yc.reid.ui.MainFrg
import com.yc.reid.utils.LogcatHelper
import com.yc.reid.utils.PopupWindowTool
import kotlinx.android.synthetic.main.a_main.drawer_layout
import kotlinx.android.synthetic.main.a_main.nav_view
import kotlinx.android.synthetic.main.include_top.top_right
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import rfid.uhfapi_y2007.ApiApplication
import rfid.uhfapi_y2007.entities.Flag
import rfid.uhfapi_y2007.entities.MemoryBank
import rfid.uhfapi_y2007.entities.Session
import rfid.uhfapi_y2007.entities.SessionInfo
import rfid.uhfapi_y2007.entities.TagParameter
import rfid.uhfapi_y2007.protocol.vrp.MsgPowerOff
import rfid.uhfapi_y2007.protocol.vrp.MsgQValueConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgSessionConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgTagInventory
import rfid.uhfapi_y2007.protocol.vrp.MsgTagRead
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class MainActivity : BaseActivity(), MainContract.View, IKeyRecv, NavigationView.OnNavigationItemSelectedListener {

    var shortPress = false

    val ps: MyPlaySound = MyPlaySound(220)

    //全局变量,判断前后台
    var isActive = false

    val mPresenter by lazy { MainPresenter() }

    var isBeep = false

    var isScan = false

    var threadBeep = MyThreadBeep()

    var mainFrg: MainFrg? = null

    override fun getLayoutId(): Int = R.layout.a_main

    override fun initView() {
        setTitleRight(getString(R.string.stocktake), R.mipmap.incon_36)
        top_right.visibility = View.GONE
//        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.mipmap.menu)
//        toolbar.setNavigationOnClickListener {
//            onOpenDrawer()
//        }
        nav_view.setNavigationItemSelectedListener(this)
        if (findFragment(MainFrg::class.java) == null) {
            mainFrg = MainFrg::class.java.newInstance()
            loadRootFragment(R.id.fl_container, mainFrg!!)
        }
        EventBus.getDefault().register(this);
        mPresenter.init(this, this)

        //激活
        ApiApplication().init(this)
        KeyRecerver.setKeyRecvCallback(this)
        //注册广播接收
        registerReceiver()
        //连接
//        mPresenter.initConn()
        // 关闭二维码扫描
        closeQrCode()
    }

    override fun initParms(bundle: Bundle) {}

    override fun onPause() {
        super.onPause()
        this.sendBroadcast(Intent("portOn")) //打开条码服务的电源
        closeQrCode()
        isScan = false
        mPresenter.initStop()
    }

    override fun onResume() {
        super.onResume()
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true
            closeQrCode()
        }
        super.onResume()
        sendBroadcast(Intent("portOff")) //关闭条码服务的电源
        closeQrCode()
//        queryScanMode()
    }

    override fun onStop() {
        if (!AppUtils.isAppForeground()) {
            //app 进入后台
            isActive = false //记录当前已经进入后台
            closeQrCode()
            isScan = false
            mPresenter.initStop()
        }
        super.onStop()
    }

    private fun queryScanMode() {
        val err = ""
        var q: Byte = -1
        var si: SessionInfo? = null
        val msg = MsgQValueConfig()
        if (Common.reader.Send(msg)) {
            q = msg.receivedMessage.q
        }
        val msg1 = MsgSessionConfig()
        if (Common.reader.Send(msg1)) {
            si = msg1.receivedMessage.sessionParam
        }
        if (q.toInt() != -1 && si != null) {
            if (q.toInt() == 4 && si.Session == Session.S1 && si.Flag == Flag.Flag_A) {

            } else if (q.toInt() == 0 && si.Session == Session.S0 && si.Flag == Flag.Flag_A_B) {

            } else {
//                if(spnScanMode.getSelectedItemPosition() != -1) {
//                    isClickSelect = false;
//                    spnScanMode.setSelection(-1, true);
//                }
            }
        }
    }

    var mTotalCount = 0

    var mStartTime: Date? = null
    private val r = Runnable {
        val nowTime = Date()
        val diff = nowTime.time - mStartTime!!.time
        if (diff > 0) {
            val h = diff / 3600000
            val m = diff % 3600000 / 60000
            val s = diff % 60000 / 1000
            LogUtils.e("总次数：" + Common.padLeft(' ', 5, "" + mTotalCount),
                "扫描时间：" + (if (h < 10) "0$h" else h) + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s)
        }
        //mStatHandler.postDelayed(r, 1000);//一秒以后，再次回调此方法
    }

    // 关闭二维码扫描
    private fun closeQrCode() {
        val toKillService = Intent()
        toKillService.action = "com.rfid.CLOSE_SCAN"
        sendBroadcast(toKillService)
    }

    //region 按键广播
    private var keyReceiver: KeyReceiver? = null
    fun registerReceiver() {
        keyReceiver = KeyReceiver()
        val filter = IntentFilter()
        filter.addAction("android.rfid.FUN_KEY")
        filter.addAction("android.intent.action.FUN_KEY")
        registerReceiver(keyReceiver, filter)
    }

    private inner class KeyReceiver : BroadcastReceiver() {

        var clickTime: Date? = null

        override fun onReceive(context: Context, intent: Intent) {
            var keyCode = intent.getIntExtra("keyCode", 0)
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0)
            }
            if (keyCode != KeyEvent.KEYCODE_F4) return
            val keyDown = intent.getBooleanExtra("keydown", true)
            if (keyDown) {
                if (clickTime != null) {
                    val t = Date().time - clickTime!!.time
                    if (t < 1000) return
                }
                clickTime = Date()
            } else {
                //OnKeyUp(keyCode);
            }
            LogUtils.e("按键" + keyCode)
        }
    }

    inner class MyThreadBeep : Thread() {
        override fun run() {
            while (isScan) {
                if (isBeep) {
                    isBeep = false
                    ps.play()
                    mySleep(20)
                    ps.stop()
                    mySleep(5)
                } else {
                    mySleep(20)
                }
            }
        }
    }

    private fun mySleep(ms: Int) {
        try {
            Thread.sleep(ms.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     *  启动/停止
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStartReceivingEvent(event: StartReceivingEvent?) {
        if (event!!.isStart) {
            val initConn = mPresenter.initConn()
            if (initConn){
                if (isActive) closeQrCode() else return
                mPresenter.onClean()
                startStat()
                if (Common.reader != null && Common.reader.isConnected) {
                    Common.reader.Send(MsgPowerOff()) // 先停止
                    Common.scanType = ScanType.ScanEpc
                    Common.reader.Send(MsgTagInventory())
                    Common.selectEpcParam = TagParameter()
//                Common.selectEpcParam.TagData = Common.selectParam.TagData.clone()
                    Common.selectEpcParam.MemoryBank = MemoryBank.EPCMemory
                    if (Common.isEnableBeep) ThreadPoolManager.getInstance().execute(threadBeep) // 蜂鸣线程
                    isScan = true
                }else{
                    showToast("Connection failed")
                }
            }
        } else {
            isScan = false
            mPresenter.onClean()
            mPresenter.disconn()
            mPresenter.initStop()
            stopStat()
            EventBus.getDefault().post(StockTakeListEvent("", "", false, 0))
        }
    }

    var mTimer: Timer? = null
    fun startStat() {
        if (mTimer != null) {
            stopStat()
            mTimer = null
        }
        mTimer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread { mStatHandler.post(r) }
            }
        }
        mTimer!!.schedule(timerTask, 1000, 1000)
        mStartTime = Date()
        mTotalCount = 0
    }

    //endregion
    var mStatHandler = Handler()
    private fun stopStat() {
        if (mTimer == null)return
        mTimer!!.cancel()
        mTimer = null
        mStatHandler.post(r)
        //mStatHandler.removeCallbacks(r);//移除回调
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_F5
            || keyCode == KeyEvent.KEYCODE_F7
        ) {
            if (event!!.action == KeyEvent.ACTION_DOWN) {
                event!!.startTracking() //只有执行了这行代码才会调用onKeyLongPress的；
                if (event!!.repeatCount == 0) {
                    shortPress = true
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun OnKeyDown(keycode: Int) {
        TODO("Not yet implemented")
    }

    override fun OnKeyUp(keycode: Int) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogcatHelper.getInstance(this).stop()
        unregisterReceiver(keyReceiver)
        EventBus.getDefault().unregister(this)
    }

    //连接失败提示
    override fun setConnFail(conn: Boolean) {
        if (!conn){
            PopupWindowTool.showDialog(this).asConfirm(
                getText(R.string.state), getText(R.string.error1),
                "", getText(R.string.confirm),{
                    onConnStateEvent(CONNECTING)
                    mPresenter.initConn()
                }, null, true
            ).show()
        }
        onConnStateEvent(if (conn) CONNECTION_SUCCEEDED else CONNECTION_FAILED)
    }

    //数据回调
    override fun setData(info: InventoryInfo?) {
        mTotalCount += 1
        var tagId = info!!.TagId
        if (StringUtils.isEmpty(tagId))return
        LogUtils.e(tagId)
        if (tagId.contains(" ")){
            tagId = tagId.replace(" ", "")
        }
        if (StringUtils.isEmpty(searchTagId)){
            EventBus.getDefault().post(StockTakeListEvent(tagId, info!!.Rssi, isScan, SCAN_STATUS_SCAN))
        }else{
            LogUtils.e(tagId, info!!.Rssi)
            EventBus.getDefault().post(InventorySearchEvent(tagId, info!!.Rssi, isScan))
        }
    }

    override fun setRefreshLayoutMode(totalRow: Int) {}

    override fun setData(objects: Object) {}

    //region 标题事件
    override fun setOnRightClickListener() {
        super.setOnRightClickListener()
//        if (top_right.text.toString().equals(getString(R.string.connectingErr))){
//            top_right.text = getString(R.string.connecting)
//            mPresenter.initConn()
//        }
        onOpenDrawer()
    }

    //region  抽屉布局
    fun onOpenDrawer() {
        if (!drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.openDrawer(GravityCompat.END)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.postDelayed({
            when(item.itemId){
                R.id.nav_load ->{
                    UIHelper.startDownloadAct()
                }
                R.id.nav_home ->{
                    UIHelper.startUploadAct()
                }
                R.id.nav_login ->{
                    UIHelper.startLoginAct()
                    LitePal.deleteAll(UserDataSql::class.java)
                    ActivityUtils.finishAllActivities()
                }
            }
            drawer_layout.closeDrawer(GravityCompat.END)
        }, 300)
        return true
    }

    /**
     *  设置连接状态
     */
    fun onConnStateEvent(type: Int) {
        when(type){
            CONNECTING -> top_right.text = getString(R.string.connecting)
            CONNECTION_SUCCEEDED -> top_right.text = getString(R.string.connectingOK)
            CONNECTION_FAILED -> top_right.text = getString(R.string.connectingErr)
        }
    }

    // 再点一次退出程序时间设置
    val WAIT_TIME = 2000L
    var TOUCH_TIME: Long = 0
    override fun onBackPressedSupport() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)){
            drawer_layout.closeDrawer(GravityCompat.END)
        }else{
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                finish();
            } else {
                TOUCH_TIME = System.currentTimeMillis();
                showToast("再按一次退出程序")
            }
        }
    }

    //region 查找TID方式
    var searchTagId: String? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInventorySearchIdEvent(event: InventorySearchIdEvent){
        if (event.isState){
            searchTagId = event.labelId
            scan(event.labelId)
        }else{
            searchTagId = null
            stop()
        }
    }

    //开始查找TID
    open fun scan(tagID: String): Boolean {
        LogUtils.e("要找的ID", tagID)
        if (isActive) closeQrCode() else return false
        var isSuc = false
        mTotalCount = 0
        mPresenter.onClean()
//        Common.selectParam = TagParameter()
//        Common.selectParam.TagData = Util.ConvertHexStringToByteArray(tagID)
        //TID
//        Common.selectParam.MemoryBank = MemoryBank.TIDMemory
        if (Common.reader != null && Common.reader.isConnected) {
            Common.reader.Send(MsgPowerOff()) // 先停止
//            Common.scanType = ScanType.ScanEpc
//            Common.scanType = ScanType.ScanTid
            if (Common.reader.Send(MsgTagRead())) isSuc = true
        }
        if (isSuc) {
            isScan = true
            if (Common.isEnableBeep) ThreadPoolManager.getInstance().execute(threadBeep) // 蜂鸣线程
        } else LogUtils.e("消息发送失败")
        startStat()
        return isSuc
    }

    // 停止查找TID
    open fun stop() {
        isScan = false
        if (Common.reader != null && Common.reader.isConnected) {
            if (Common.reader.Send(MsgPowerOff())) {
                //Toast.makeText(MainActivity.this,"停止读卡",Toast.LENGTH_SHORT).show();
            }
        }
        stopStat()
    }

}
