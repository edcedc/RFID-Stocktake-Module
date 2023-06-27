package com.yc.reid.bean.sql

import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * @Author nike
 * @Date 2023/6/1 14:50
 * @Description
 */
class UserDataSql : LitePalSupport(), Serializable {

    var LoginID: String? = null
    var Password: String? = null
    var EMail: String? = null
    var Phone: String? = null
    var Position: String? = null
    var PImgfile: String? = null
    var RoNo: String? = null

}