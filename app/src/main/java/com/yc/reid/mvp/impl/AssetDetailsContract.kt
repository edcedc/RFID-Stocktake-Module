package com.yc.reid.mvp.impl

import com.luck.picture.lib.entity.LocalMedia
import com.yc.reid.base.IBaseListView
import com.yc.reid.base.IListPresenter
import org.json.JSONObject
import java.util.ArrayList

/**
 * @Author nike
 * @Date 2023/6/9 10:56
 * @Description
 */
interface AssetDetailsContract {

    interface View : IBaseListView {
    }

    interface Presenter: IListPresenter<View> {

        fun onRequest(page: Int?)
        fun onRequest(page: String?)
        fun initData(position: Int, bean: String?)
        fun submit(data2Obj: JSONObject, localMediaList: ArrayList<LocalMedia?>, toString: String)

    }
}