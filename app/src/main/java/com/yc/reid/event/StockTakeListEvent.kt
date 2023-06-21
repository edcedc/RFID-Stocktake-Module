package com.yc.reid.event

/**
 * @Author nike
 * @Date 2023/6/5 15:45
 * @Description  扫描发送数据
 */
class StockTakeListEvent(val LabelTag: String, val Rssi: String, val isScan: Boolean, var FoundStatus: Int) {
}