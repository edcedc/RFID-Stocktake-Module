package com.yc.reid.bean.sql

import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * @Author nike
 * @Date 2023/6/19 12:52
 * @Description 上传清单到后台
 */
class UploadStockDataSql : LitePalSupport(), Serializable {

    var userid: String? = null
    var roNo: String? = null
    var companyid: String? = null

    var orderNo: String? = null

    var time: String? = ""

    var isSave: Int? = 0//0未上传 1上传

    var data: String? = null

}