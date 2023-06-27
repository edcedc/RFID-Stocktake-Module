package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.blankj.utilcode.util.LogUtils
import com.yc.reid.INVENTORY_FAIL
import com.yc.reid.R
import com.yc.reid.adapter.InventoryTwoAdapter
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.event.SearchListEvent
import com.yc.reid.event.StockTakeEvent
import com.yc.reid.event.StockTakeUpdateTtitleEvent
import com.yc.reid.mvp.impl.InventoryTwoContract
import com.yc.reid.mvp.presenter.InventoryTwoPresenter
import com.yc.reid.weight.LinearDividerItemDecoration
import kotlinx.android.synthetic.main.b_not_title_recycler.recyclerView
import kotlinx.android.synthetic.main.b_not_title_recycler.refreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/6/2 15:26
 * @Description 异常
 */
class InventoryFailFrg: BaseFragment(), InventoryTwoContract.View{

    val mPresenter by lazy { InventoryTwoPresenter() }

    var listBean = ArrayList<StockChildSql>()

    val adapter by lazy { activity?.let { InventoryTwoAdapter(it,this,listBean,) } }

    var stocktakeno: String? = null

    override fun getLayoutId(): Int = R.layout.b_not_title_recycler

    override fun initParms(bundle: Bundle) {
        stocktakeno = bundle.getString("stocktakeno")
    }

    override fun initView(rootView: View) {
        setSwipeBackEnable(false)
        mPresenter.init(this, activity)
        EventBus.getDefault().register(this)
        setRecyclerViewType(recyclerView = recyclerView)
        recyclerView.addItemDecoration(LinearDividerItemDecoration(activity, DividerItemDecoration.VERTICAL,10))
        recyclerView.adapter = adapter
        adapter!!.appendList(listBean)
        refreshLayout.setEnableRefresh(false)
        refreshLayout.setEnableLoadMore(false)
        mPresenter.onChildRequest(stocktakeno, INVENTORY_FAIL)
    }

    override fun setData(objects: Object) {
        listBean.clear()
        listBean.addAll(objects as List<StockChildSql>)
        adapter?.notifyDataSetChanged()
    }

    override fun setRefreshLayoutMode(totalRow: Int) {}
    override fun setData(objects: java.util.ArrayList<StockChildSql>, jsonObject: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchListEvent(event: SearchListEvent){
        adapter!!.filter.filter(event.text)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStockTakeEvent(event: StockTakeEvent){
        val stocktakeListSql = event.bean
        if (stocktakeListSql.type == INVENTORY_FAIL){
            listBean.forEachIndexed() { i, bean ->
                if (bean.LabelTag.equals(event.bean.LabelTag)){
                    return
                }
            }
            listBean.add(0, event.bean)
            adapter?.notifyDataSetChanged()
        }
    }
    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(StockTakeUpdateTtitleEvent("", listBean.size))
    }
}