package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.yc.reid.R
import com.yc.reid.adapter.OnItemClickListener
import com.yc.reid.adapter.UploadAdapter
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.UploadStockDataSql
import com.yc.reid.mvp.impl.UploadContract
import com.yc.reid.mvp.presenter.UploadPresenter
import com.yc.reid.weight.LinearDividerItemDecoration
import kotlinx.android.synthetic.main.b_not_title_recycler.recyclerView
import kotlinx.android.synthetic.main.b_not_title_recycler.refreshLayout
import java.util.Collections

/**
 * @Author nike
 * @Date 2023/6/13 10:30
 * @Description 上传
 */
class UploadFrg : BaseFragment(), UploadContract.View, OnItemClickListener {

    val mPresenter by lazy { UploadPresenter() }

    val adapter by lazy { activity?.let { UploadAdapter(it, this, listBean) } }

    var listBean = ArrayList<UploadStockDataSql>()

    override fun getLayoutId(): Int = R.layout.b_title_recycler

    override fun initParms(bundle: Bundle) {
    }

    override fun initView(rootView: View) {
        setTitle(getString(R.string.upload))
        mPresenter.init(this)
        setRecyclerViewType(recyclerView = recyclerView)
        recyclerView.addItemDecoration(LinearDividerItemDecoration(activity, DividerItemDecoration.VERTICAL,10))
        recyclerView.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        refreshLayout.setEnableRefresh(false)
        refreshLayout.setEnableLoadMore(false)
        mPresenter.onRequest(pagerNumber)
    }

    override fun onSuccess(position: Int) {
        val bean = listBean.get(position)
        bean.isSave = 1
        bean.save()
        adapter!!.notifyItemChanged(position)
        adapter!!.notifyItemRangeChanged(position, listBean.size - position)
    }

    override fun setRefreshLayoutMode(totalRow: Int) {}

    override fun setData(objects: Object) {
        listBean.addAll(objects as List<UploadStockDataSql>)
        Collections.reverse(listBean);
        adapter?.notifyDataSetChanged()
    }

    override fun onItemClick(objects: Object, position: Int) {
        val bean = objects as UploadStockDataSql
        mPresenter.onUpload(position, bean)
        mPresenter.onUploadImage(bean)
    }

}