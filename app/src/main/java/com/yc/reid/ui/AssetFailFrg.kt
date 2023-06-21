package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.blankj.utilcode.util.LogUtils
import com.yc.reid.INVENTORY_FAIL
import com.yc.reid.R
import com.yc.reid.adapter.AssetAdapter
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.event.SearchListEvent
import com.yc.reid.event.StockTakeEvent
import com.yc.reid.event.StockTakeUpdateTtitleEvent
import com.yc.reid.mvp.impl.InventoryDetailsContract
import com.yc.reid.mvp.presenter.InventoryDetailsPresenter
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
class AssetFailFrg: BaseFragment(), InventoryDetailsContract.View{

    val mPresenter by lazy { InventoryDetailsPresenter() }

    var listBean = ArrayList<StockChildSql>()

    val adapter by lazy { activity?.let { AssetAdapter(it,this,listBean,) } }

    var stocktakeno: String? = null

    var failIsVisible = false

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
            if (failIsVisible)EventBus.getDefault().post(StockTakeUpdateTtitleEvent("", listBean.size))
        }
    }


    override fun onResume() {
        super.onResume()
        failIsVisible = true
        EventBus.getDefault().post(StockTakeUpdateTtitleEvent("", listBean.size))
    }


    override fun onSupportInvisible() {
        super.onSupportInvisible()
        failIsVisible = false
    }

}