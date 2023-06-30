package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.yc.reid.INVENTORY_READ
import com.yc.reid.R
import com.yc.reid.adapter.AssetAdapter
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.event.SearchListEvent
import com.yc.reid.event.StockListUploadDataEvent
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

/**
 * @Author nike
 * @Date 2023/6/2 15:26
 * @Description 盘点清单二级  - 全部
 */
class AssetAllFrg: BaseFragment(), InventoryDetailsContract.View {

    val mPresenter by lazy { InventoryDetailsPresenter() }

    val adapter by lazy { activity?.let { AssetAdapter(it, this, listBean)} }

    var listBean = ArrayList<StockChildSql>()

    var stocktakeno: String? = null//父级ID

    var allIsVisible = false

    override fun getLayoutId(): Int = R.layout.b_not_title_recycler

    override fun initParms(bundle: Bundle) {
        stocktakeno = bundle.getString("stocktakeno")
//        val list= bundle.getSerializable("list") as ArrayList<StockChildSql>
//        listBean.addAll(list)
    }

    override fun initView(rootView: View) {
        setSwipeBackEnable(false)
        mPresenter.init(this, activity)
        EventBus.getDefault().register(this)
        setRecyclerViewType(recyclerView = recyclerView)
//        recyclerView.addItemDecoration(LinearDividerItemDecoration(activity, DividerItemDecoration.VERTICAL,10))
        recyclerView.adapter = adapter
        refreshLayout.setEnableRefresh(false)
        refreshLayout.setEnableLoadMore(false)
        showUiLoading()
        mPresenter.onChildRequest(stocktakeno, -1)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStockTakeEvent(event: StockTakeEvent){
        val stockSql = event.bean
        var isExist = false
        if (stockSql.type == INVENTORY_READ){
            listBean.forEachIndexed(){i, bean ->
                if ((StringUtils.equalsIgnoreCase(bean.LabelTag, stockSql.LabelTag) || StringUtils.equalsIgnoreCase(bean.AssetNo, stockSql.LabelTag)) && bean.type != INVENTORY_READ){
                    isExist = true
                    bean.type = INVENTORY_READ
                    bean.scan_status = stockSql.scan_status
                    adapter?.notifyItemChanged(i)
                    adapter?.notifyItemRangeChanged(i , listBean.size - i)

                    if (!StringUtils.isEmpty(searchText)){
                        adapter!!.filter.filter(searchText)
                    }
//                    if (allIsVisible)EventBus.getDefault().post(StockTakeUpdateTtitleEvent("", listBean.size))
//                    LogUtils.e("更新了吗", i, stockSql.LabelTag, stockSql.type)
                    return@forEachIndexed
                }
            }
        }
//        if (!isExist){ 记录异常的
//            listBean.add(0,stockSql)
//            adapter?.notifyDataSetChanged()
//        }
    }

    var searchText: String? = ""

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchListEvent(event: SearchListEvent){
        searchText = event.text
        adapter!!.filter.filter(event.text)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStockListUploadDataEvent(event: StockListUploadDataEvent){
        mPresenter.onChildRequest(stocktakeno, -1)
    }

    override fun setRefreshLayoutMode(totalRow: Int) {
        super.setRefreshLayoutMode(listBean.size, totalRow, refreshLayout)
    }

    override fun setData(objects: Object) {
        listBean.clear()
        listBean.addAll(objects as List<StockChildSql>)
        adapter!!.appendList(listBean)
        adapter?.notifyDataSetChanged()
        EventBus.getDefault().post(StockTakeUpdateTtitleEvent("", listBean.size))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        allIsVisible = true
        EventBus.getDefault().post(StockTakeUpdateTtitleEvent("", listBean.size))
    }

    override fun onSupportInvisible() {
        allIsVisible = false
        super.onSupportInvisible()
    }

}