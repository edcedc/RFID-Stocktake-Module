package com.yc.reid.bean


import java.io.Serializable

/**
 * Created by yc on 2017/8/17.
 */

class DataBean : Serializable {

    var name: String? = null
    var content: String? = null
    var title: String? = null
    var url: String? = null
    var background: String? = null
    var image: String? = null
    var id: Long? = 0
    var stocktakeno: String? = null
    var startDate: String? = null
    var endDate: String? = null
    var lastUpdate: String? = null
    var total: Int? = 0
    var progress: Int? = 0
    var OrderNo: String? = null
    var AssetNo: String? = null
    var LabelName: String? = null
    var LabelTag: String? = null
    var CreateUser: String? = null
    var Rssi: String? = null
    var CreateDate: String? = AssetNo
    var StatusName: String? = null
    var FoundStatus: String? = null
    var remarks: String? = null
    var RoNo: String? = null
    var LoginID: String? = null
    var Password: String? = null
    var isVisibility = false
    var stocktakeId: Long? = 0
    var type: Int = 0
    var data2: List<DataBean>? = null

}