package com.yc.reid.bean.sql

import com.google.gson.JsonObject
import org.json.JSONObject
import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * @Author nike
 * @Date 2023/6/14 17:40
 * @Description 首页列表
 */
class StocktakeListSql : LitePalSupport(), Serializable{

    var LoginID: String? = null
    var roNo: String? = null
    var companyid: String? = null

    var ids: String? = null
    var stocktakeno: String? = null
    var name: String? = null
    var startDate: String? = null
    var endDate: String? = null
    var lastUpdate: String? = null
    var remarks: String? = null
    var progress: Int? = 0
    var total: Int? = 0

    var jsonObject: String? = null

}