package com.yc.reid.api

class CloudApi private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object{

        private val url =
//            "192.168.2.18"
//            "192.168.2.31"
            "47.243.120.137"

        var SERVLET_URL = "http://" +
                url + "/RFIDInventoryWebService/MobileWebService.asmx/"


        private var TEST_URL = "" //测试

        @JvmField
        var SERVLET_IMG_URL = "" //测试

    }

}