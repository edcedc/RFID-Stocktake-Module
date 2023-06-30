package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.yc.reid.R
import com.yc.reid.adapter.AssetDetailsAdapter
import com.yc.reid.base.BaseFragment
import com.yc.reid.mvp.impl.AssetDetailsContract
import com.yc.reid.mvp.presenter.AssetDetailsPresenter
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
class   AssetDetailsFrg : BaseFragment(), AssetDetailsContract.View {

    val mPresenter by lazy { AssetDetailsPresenter() }

    val adapter by lazy { activity?.let { AssetDetailsAdapter(it, jsonArray) } }

    var jsonArray = JSONArray()

    var data: String? = null

    override fun getLayoutId(): Int = R.layout.b_not_title_recycler

    override fun initParms(bundle: Bundle) {
        var bean = JSONObject(bundle.getString("bean"))
        data = bean.optString("data")
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
        showUiLoading()
        mPresenter.onRequest(data)
    }

    override fun setRefreshLayoutMode(totalRow: Int) {}

    override fun setData(objects: Object) {
        var list = objects as JSONArray
        for (i in 0 until list.length()) {
            val obj = list.optJSONObject(i)
            jsonArray.put(obj)
        }
        adapter!!.notifyDataSetChanged()
    }

}