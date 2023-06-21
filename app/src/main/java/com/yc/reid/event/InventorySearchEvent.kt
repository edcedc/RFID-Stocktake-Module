package com.yc.reid.event

/**
 * @Author nike
 * @Date 2023/6/5 15:45
 * @Description  指定ID扫描回调
 */
class InventorySearchEvent(val LabelTag: String, val Rssi: String, val isScan: Boolean) {
}