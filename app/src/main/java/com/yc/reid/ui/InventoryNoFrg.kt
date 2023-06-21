package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.yc.reid.INVENTORY_ALL
import com.yc.reid.INVENTORY_READ
import com.yc.reid.R
import com.yc.reid.adapter.InventoryTwoAdapter
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.event.SearchListEvent
import com.yc.reid.event.StockListUploadDataEvent
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
 * @Description 不在库
 */
class InventoryNoFrg: BaseFragment(), InventoryTwoContract.View{

    val mPresenter by lazy { InventoryTwoPresenter() }

    val adapter by lazy { activity?.let { InventoryTwoAdapter(it,this,listBean) } }

    var listBean = ArrayList<StockChildSql>()

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
        mPresenter.onChildRequest(stocktakeno, INVENTORY_ALL)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStockTakeEvent(event: StockTakeEvent){
        if (event.bean.type == INVENTORY_READ){
            listBean.forEachIndexed(){i, bean ->
                if (bean.LabelTag.equals(event.bean.LabelTag)){
                    listBean.removeAt(i)
                    adapter?.notifyItemRemoved(i)
                    adapter?.notifyItemChanged(i,listBean.size - i)
                    return
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchListEvent(event: SearchListEvent){
        adapter!!.filter.filter(event.text)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStockListUploadDataEvent(event: StockListUploadDataEvent){
        mPresenter.onChildRequest(stocktakeno, INVENTORY_ALL)
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

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(StockTakeUpdateTtitleEvent("", listBean.size))
    }

}