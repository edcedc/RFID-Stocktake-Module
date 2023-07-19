package com.yc.reid.bean.sql

import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject
import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * @Author nike
 * @Date 2023/6/5 17:10
 * @Description
 */
class StockChildSql : LitePalSupport(), Serializable {

    var stocktakeId: Long? = 0//盘点列表ID
    var type: Int = 0//显示状态

    var roNo: String? = null
    var companyid: String? = null

    var scan_time: String? = null//扫描时间
    var scan_status: Int? = 0//扫描类型  116：Scan；117：QRCode；118 Manually

    var OrderNo: String? = ""
    var AssetNo: String? = ""
    var LabelName: String? = null
    var LabelTag: String? = null // 并不是只接受labelTag值，也是有数据情况下判断的
    var CreateUser: String? = null
    var CreateDate: String? = null
    var StatusName: String? = null
    var FoundStatus: Int? = 0
    var remarks: String? = ""
    var Rssi: String? = null
    var stocktakeno: String? = null

    var AssetStatus: String? = null

    var ids: String? = null//唯一值

    var Tag: String? = null

    var Inventory: String? = null

    var data: String? = null

    var iamgeList: String? = null

    var isVisibility = false

}