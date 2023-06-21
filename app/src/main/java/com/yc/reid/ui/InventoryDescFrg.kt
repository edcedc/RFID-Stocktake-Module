package com.yc.reid.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.blankj.utilcode.util.LogUtils
import com.yc.reid.INVENTORY_READ
import com.yc.reid.R
import com.yc.reid.adapter.InventoryDescAdapter
import com.yc.reid.base.BaseFragment
import com.yc.reid.mvp.impl.InventoryDescContract
import com.yc.reid.mvp.presenter.InventoryDescPresenter
import com.yc.reid.weight.LinearDividerItemDecoration
import kotlinx.android.synthetic.main.b_not_title_recycler.recyclerView
import kotlinx.android.synthetic.main.b_not_title_recycler.refreshLayout
import org.json.JSONArray
import org.json.JSONObject


/**
 * @Author nike
 * @Date 2023/6/8 19:41
 * @Description
 */
class InventoryDescFrg : BaseFragment(), InventoryDescContract.View {

    val mPresenter by lazy { InventoryDescPresenter() }

    val adapter by lazy { activity?.let { InventoryDescAdapter(it, jsonArray) } }

    var jsonArray = JSONArray()

    override fun getLayoutId(): Int = R.layout.b_not_title_recycler

    override fun initParms(bundle: Bundle) {
        var bean = JSONObject(bundle.getString("bean"))
        val AssetNo = bean.optString("AssetNo")
        val type = bean.optInt("type")
        var jsonObject = JSONObject(bean.optString("data"))
        val data2 = jsonObject.optJSONArray("data2")
        if (data2 == null && data2?.length() == 0) return
        var data2Obj = JSONObject()
        for (i in 0 until data2.length()) {
            val opt = data2.optJSONObject(i)
            if (opt.optString("AssetNo").equals(AssetNo)) {
                data2Obj = opt
                break
            }
        }
        //存默认的数据
        var oneObj = JSONObject()
        oneObj.put("title", getString(R.string.asset_material))
        //手动添加第一层数据
        val tagObj = data2Obj.optJSONObject("Tag")
        var oneOry2 = JSONArray()
        if (tagObj != null){
//            tagObj.put(getString(R.string.order_id), data2Obj.optString("OrderNo"))
//            tagObj.put(getString(R.string.asset), data2Obj.optString("AssetNo"))
//            tagObj.put( getString(R.string.state),if (type == INVENTORY_READ) getString(R.string.in_stock) else getString(R.string.not_library))
//            tagObj.put(getString(R.string.label_name), data2Obj.optString("LabelName"))
            tagObj.put(getString(R.string.label), data2Obj.optString("LabelTag"))
//            tagObj.put(getString(R.string.user), data2Obj.optString("CreateUser"))
//            tagObj.put(getString(R.string.start_date), data2Obj.optString("CreateDate"))
//            tagObj.put(getString(R.string.found_status), data2Obj.optString("FoundStatus"))
//            tagObj.put(getString(R.string.remarks), data2Obj.optString("remarks"))
            val headerkeys: Iterator<String> = tagObj.keys()
            while (headerkeys.hasNext()) {
                val headerkey = headerkeys.next()
                val headerValue: String = tagObj.getString(headerkey)
                var onekeyObt = JSONObject()
                onekeyObt.put("title", headerkey)
                onekeyObt.put("text", headerValue)
                oneOry2.put(onekeyObt)
            }
        }
        oneObj.put("list", oneOry2)
        jsonArray.put( oneObj)

        //org.json.JSONObject解析方法
        val headerkeys: Iterator<String> = jsonObject.keys()
        while (headerkeys.hasNext()) {
            val headerkey = headerkeys.next()
            val headerValue: String = jsonObject.getString(headerkey)
            var jsonObject = JSONObject()
            jsonObject.put("title", headerkey)
            if (headerkey.equals("data2")) {
                break
            }
            var jsonArray2 = JSONArray()
            var jsonObject2 = JSONObject(headerValue)
            val headerkeys2: Iterator<String> = jsonObject2.keys()
            while (headerkeys2.hasNext()) {
                val headerkey2 = headerkeys2.next()
                val headerValue2: String = jsonObject2.getString(headerkey2)
                var jsonObject3 = JSONObject()
                jsonObject3.put("title", headerkey2)

                //存第二层的数据
                val dataHeaderkeys: Iterator<String> = data2Obj.keys()
                while (dataHeaderkeys.hasNext()) {
                    val dataHeaderkey = dataHeaderkeys.next()
                    val dataHeaderValue: String = data2Obj.getString(dataHeaderkey)
                    if (headerkey2.equals(dataHeaderkey)) {
                        jsonObject3.put("text", dataHeaderValue)
                    }
                }
                jsonArray2.put(jsonObject3)
                //都存起来
                jsonObject.put("list", jsonArray2)
            }
            jsonArray.put(jsonObject)
        }


    }

    override fun initView(rootView: View) {
        mPresenter.init(this, activity)
        setRecyclerViewType(recyclerView = recyclerView)
        recyclerView.addItemDecoration(
            LinearDividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL,
                10
            )
        )
        recyclerView.adapter = adapter
        refreshLayout.setEnableRefresh(false)
        refreshLayout.setEnableLoadMore(false)
    }

    override fun setImage(bitmap: Bitmap?) {
        TODO("Not yet implemented")
    }

    override fun setRefreshLayoutMode(totalRow: Int) {}

    override fun setData(objects: Object) {}

}