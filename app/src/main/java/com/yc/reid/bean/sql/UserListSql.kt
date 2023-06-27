package com.yc.reid.bean.sql

import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * @Author nike
 * @Date 2023/6/28 11:27
 * @Description
 */
class UserListSql : LitePalSupport(), Serializable {

    var RoNo: String? = null
    var LoginID: String? = null
    var Password: String? = null

}