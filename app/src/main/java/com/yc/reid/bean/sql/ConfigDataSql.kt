package com.yc.reid.bean.sql

import org.litepal.crud.LitePalSupport

/**
 * @Author nike
 * @Date 2023/6/1 17:45
 * @Description
 */
class ConfigDataSql : LitePalSupport() {

    var languagePosition: Int? = 0
    var url: String? = null
    var companyid: String? = null

}