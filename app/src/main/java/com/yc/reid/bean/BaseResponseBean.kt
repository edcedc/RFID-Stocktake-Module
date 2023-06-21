package com.yc.reid.bean

import java.io.Serializable

class BaseResponseBean<T> : Serializable {

    var code: Int = 0
    var desc: String? = null
    var data: T? = null
    var msg: Int? = 0

    override fun toString(): String {
        return "BaseResponseBean(code=$code, desc=$desc, data=$data)"
    }


}
