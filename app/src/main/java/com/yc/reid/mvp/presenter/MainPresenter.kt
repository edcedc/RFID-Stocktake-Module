package com.yc.reid.mvp.presenter

import android.nfc.cardemulation.HostNfcFService
import androidx.annotation.Keep
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ThreadUtils
import com.yc.reid.base.BaseListPresenter
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.StocktakeListSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.keyctrl.Common
import com.yc.reid.keyctrl.ScanType
import com.yc.reid.keyctrl.entity.InventoryInfo
import com.yc.reid.keyctrl.entity.TagFindParam
import com.yc.reid.mvp.impl.MainContract
import org.json.JSONObject
import org.litepal.LitePal
import rfid.uhfapi_y2007.Rs232Port
import rfid.uhfapi_y2007.core.Util
import rfid.uhfapi_y2007.entities.ConnectResponse
import rfid.uhfapi_y2007.entities.Flag
import rfid.uhfapi_y2007.entities.FrequencyArea
import rfid.uhfapi_y2007.entities.MemoryBank
import rfid.uhfapi_y2007.entities.RxdTagData
import rfid.uhfapi_y2007.entities.Session
import rfid.uhfapi_y2007.entities.SessionInfo
import rfid.uhfapi_y2007.entities.TagParameter
import rfid.uhfapi_y2007.protocol.vrp.Msg6CTagFieldConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgFilteringTimeConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgPowerConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgPowerOff
import rfid.uhfapi_y2007.protocol.vrp.MsgQValueConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgSessionConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgTagInventory
import rfid.uhfapi_y2007.protocol.vrp.MsgUhfBandConfig
import rfid.uhfapi_y2007.protocol.vrp.Reader
import rfid.uhfapi_y2007.utils.Event
import java.text.SimpleDateFormat
import java.util.Date


/**
 * @Author nike
 * @Date 2023/5/31 10:38
 * @Description
 */
class MainPresenter : BaseListPresenter<MainContract.View>(), MainContract.Presenter{

    var msgNotify = Event(this, "reader_OnInventoryReceived")
    val connBrokenNotify = Event(this, "reader_OnBrokenNetwork")
    var tagFindParam: TagFindParam? = null
    var timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") // 日期的输出格式

    override fun onRequest(page: Int) {
        val userDataSql = LitePal.findFirst(UserDataSql::class.java)
        val stocktakeListSql = LitePal.where("roNo = ?", userDataSql.RoNo).find(StocktakeListSql::class.java)
        mRootView?.setData(stocktakeListSql as Object)
    }

    /**
     * 配置
     */
    override fun config() {
        //频率0-32
        val pMsg = MsgPowerConfig(byteArrayOf(32))
        if (!Common.reader.Send(pMsg, 3000)){
            LogUtils.e("天线功率设置失败")
        }else{
            LogUtils.e("天线功率设置成功")
        }
        //工作频段CN0 FCC1 CU2
        val bMsg = MsgUhfBandConfig(FrequencyArea.valueOf(0))
        if (Common.reader.Send(bMsg, 3000)){
            LogUtils.e("工作频段设置失败")
        }else{
            LogUtils.e("工作频段设置成功")
        }
        // 设置重复标签过滤时间  数值 *100ms
        val fMsg = MsgFilteringTimeConfig(0)
        if (!Common.reader.Send(fMsg, 3000)){
            LogUtils.e("重复标签过滤时间设置成功")
        }else{
            LogUtils.e("重复标签过滤时间设置失败")
        }
        // 设置读6C标签数据包含字段   true false
        val msg = Msg6CTagFieldConfig(false, true)
        if (!Common.reader.Send(msg, 3000)){
            LogUtils.e("天线和RSSI设置成功")
        }else{
            LogUtils.e("天线和RSSI设置失败")
        }
        //设置Q值 0-15
        val msgQ = MsgQValueConfig(4)
        if (!Common.reader.Send(msgQ, 500)){
            LogUtils.e("Q值设置失败")
        }else{
            LogUtils.e("Q值设置成功")
        }
        //Session 0-3
        //多标签
        val si = SessionInfo()
        si.Session = Session.S1
        si.Flag = Flag.Flag_A
        val msgS = MsgSessionConfig(si)
        if (!Common.reader.Send(msgS)){
            LogUtils.e("Session设置失败")
        }else{
            LogUtils.e("Session设置成功")
        }
        //设置声音
        Common.isEnableBeep = true
        //显示
        Common.tagEncoding = "HEX"
        // 设置EPC
//        Common.scanType = ScanType.ScanEpc
//        Common.reader.Send(MsgTagInventory())
//        Common.selectParam.MemoryBank = MemoryBank.EPCMemory
//        Common.selectEpcParam = TagParameter()
//        Common.selectEpcParam.TagData = Common.selectParam.TagData.clone()
//        Common.selectEpcParam.MemoryBank = MemoryBank.EPCMemory

        //读取
//        Common.selectParam.TagData = Util.ConvertHexStringToByteArray("TID")
//        Common.selectParam.MemoryBank = MemoryBank.TIDMemory
    }


    //endregion
    //region RFID模块控制
    var isSuc = false
    override fun conn(): Boolean {
        Common.setReaderConnPort("Reader1", Rs232Port("COM13,115200"))
        //        Common.setReaderConnPort("Reader1", new TcpClientPort("192.168.95.166:9090"));
        Common.reader = Reader("Reader1")
        val response: ConnectResponse = Common.reader.Connect()
        if (response.IsSucessed) {
            Common.reader.Send(MsgPowerOff()) //关功放
            //queryScanMode();//查询读取模式
            Reader.OnBrokenNetwork.addEvent(connBrokenNotify)
            Common.reader.OnInventoryReceived.addEvent(msgNotify)
            isSuc = true
        }else{
            LogUtils.e("连接失败" + response.ErrorInfo.errMsg)
            isSuc = false
        }
        return isSuc
    }

    /**
     *  连接
     */
    override fun initConn(): Boolean{
        /*ThreadUtils.executeByCpu(object : ThreadUtils.Task<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {
                disconn()
                if (conn()) {
                    config()
                    LogUtils.e("RFID模块连接成功", Common.reader.getHardwareVersion(), Common.reader.getSoftwareVersion())
                    return true
                } else {
                    LogUtils.e("RFID模块连接失败")
                    return false
                }
                return null as Any?
            }

            override fun onSuccess(result: Any?) {
                mRootView?.setConnFail(isSuc)
            }
            override fun onCancel() {}
            override fun onFail(t: Throwable) {
                LogUtils.e(t.message)
            }
        })*/

        disconn()
        if (conn()) {
            config()
            LogUtils.e("RFID模块连接成功", Common.reader.getHardwareVersion(), Common.reader.getSoftwareVersion())
            return true
        } else {
            LogUtils.e("RFID模块连接失败")
            mRootView?.setConnFail(isSuc)
            return false
        }
    }

    override fun disconn() {
        if (Common.reader != null) {
            Common.reader.Send(MsgPowerOff())
            Reader.OnBrokenNetwork.removeEvent(connBrokenNotify)
            Common.reader.OnInventoryReceived.removeEvent(msgNotify)
            Common.reader.Disconnect()
            Common.reader = null
        }
    }

    override fun onClean() {
        Common.selectParam = null
        Common.selectEpcParam = null
    }

    override fun initStop() {
        if (Common.reader != null && Common.reader.isConnected) {
            if (Common.reader.Send(MsgPowerOff())) {
                LogUtils.e("停止读卡")
            }
        }
    }

    @Keep
    private fun reader_OnInventoryReceived(sender: Reader, tagData: RxdTagData?) {
        val epc =
            if (Common.tagEncoding.equals("HEX")) Util.ConvertByteArrayToHexWordString(tagData!!.epc) else Common.bytesToAscii(
                tagData!!.epc
            )
        val tid = Util.ConvertByteArrayToHexWordString(tagData!!.tid)
        val rssi = if (tagData!!.rssi.toInt() == 0) "" else "" + Common.getRSSI(
            tagData.rssi.toInt()
        )
        if (tagFindParam != null) {
            var isM = false
            isM =
                if (tagFindParam!!.ScanMB.equals("EPC")) tagFindParam!!.isMatching(tagData.epc) else tagFindParam!!.isMatching(
                    tagData.tid
                )
            if (!isM) return
        }
        val tag = InventoryInfo()
        tag.TagId = if (epc == null || epc === "") tid else epc
        tag.MB = if (epc == null || epc === "") "TID" else "EPC"
        tag.Rssi = rssi
        tag.ReadTime = timeFormat.format(Date())
        mRootView!!.setData(tag)
    }

}