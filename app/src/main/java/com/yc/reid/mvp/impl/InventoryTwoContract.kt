package com.yc.reid.mvp.impl

import com.yc.reid.base.IBaseListView
import com.yc.reid.base.IListPresenter
import com.yc.reid.bean.sql.StockChildSql
import org.json.JSONObject
import java.util.ArrayList

/**
 * @Author nike
 * @Date 2023/5/31 10:37
 * @Description
 */
interface InventoryTwoContract {

    interface View : IBaseListView {
        fun setData(objects: ArrayList<StockChildSql>, jsonObject: JSONObject)
    }

    interface Presenter: IListPresenter<View> {

        fun onRequest(page: Int)

        fun onRequest(page: Int, orderId: String)

        fun onChildRequest(stocktakeno: String?, MAIN_FIRST: Int)
        fun onUpload(stocktakeno: String?)

    }

}