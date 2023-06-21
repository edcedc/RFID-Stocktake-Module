package com.yc.reid.bean

import java.io.Serializable

/**
 * Created by yc on 2017/8/17.
 */

class BaseListBean<T> : Serializable {

    var totalRow: Int = 0
    var totalPage: Int = 0
    var pageNumber: Int = 0
    var pageSize: Int = 0
    var list: List<T>? = null

    var data: List<T>? = null
    var code: Int = 0
    var count: Int = 0

    override fun toString(): String {
        return "BaseListBean(totalRow=$totalRow, totalPage=$totalPage, pageNumber=$pageNumber, pageSize=$pageSize, list=$list, data=$data, code=$code, count=$count)"
    }

}
