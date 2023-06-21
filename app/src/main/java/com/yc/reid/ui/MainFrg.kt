package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.yc.reid.R
import com.yc.reid.adapter.InventoryListAdapter
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StocktakeListSql
import com.yc.reid.event.DownloadStateEvent
import com.yc.reid.keyctrl.entity.InventoryInfo
import com.yc.reid.mvp.impl.MainContract
import com.yc.reid.mvp.presenter.MainPresenter
import com.yc.reid.weight.LinearDividerItemDecoration
import kotlinx.android.synthetic.main.b_not_title_recycler.recyclerView
import kotlinx.android.synthetic.main.b_not_title_recycler.refreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/9/24
 * Time: 11:13
 */
class MainFrg : BaseFragment(), MainContract.View{

    val mPresenter by lazy { MainPresenter() }

    val adapter by lazy { activity?.let { InventoryListAdapter(it,this, listBean) } }

    val listBean = ArrayList<StocktakeListSql>()

    override fun getLayoutId(): Int = R.layout.b_not_title_recycler

    override fun initParms(bundle: Bundle) {
    }

    override fun initView(rootView: View) {
        mPresenter.init(this, activity)
        setRecyclerViewType(recyclerView = recyclerView)
        recyclerView.addItemDecoration(LinearDividerItemDecoration(activity, DividerItemDecoration.VERTICAL, 10))
        recyclerView.adapter = adapter
        refreshLayout.setEnableRefresh(false)
        refreshLayout.setEnableLoadMore(false)
        EventBus.getDefault().register(this)
        mPresenter.onRequest(pagerNumber)
    }

    // 下载成功更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDownloadStateEvent(event: DownloadStateEvent){
        if (event.isState){
            mPresenter.onRequest(pagerNumber)
        }
    }

    override fun setRefreshLayoutMode(totalRow: Int) {
        super.setRefreshLayoutMode(listBean.size, totalRow, refreshLayout)
    }

    override fun setData(objects: Object) {
        val list = objects as List<StocktakeListSql>
        listBean.clear()
        listBean.addAll(list)
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun setConnFail(isConn: Boolean) {}
    override fun setData(objects: InventoryInfo?) {}

}