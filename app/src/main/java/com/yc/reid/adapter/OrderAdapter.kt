package com.yc.reid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.StringUtils
import com.yc.reid.R
import com.yc.reid.api.UIHelper
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StocktakeListSql

class OrderAdapter (act: Context, root: BaseFragment, listBean: List<StocktakeListSql>) :
    BaseRecyclerviewAdapter<StocktakeListSql>(act, listBean as ArrayList<StocktakeListSql>) {
    override fun onCreateViewHolde(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.i_order, parent, false))
    }

    override fun onBindViewHolde(viewHolder: ViewHolder, position: Int) {
        val bean = listBean[position]
        viewHolder.setText(R.id.tv_title, bean.stocktakeno + " | " + bean.name)
        viewHolder.setText(R.id.tv_start_date, act.getString(R.string.start_date) + "：" + bean.startDate)
        viewHolder.setText(R.id.tv_end_date, act.getString(R.string.end_date) + "：" + bean.endDate)
        viewHolder.setText(R.id.tv_progress, act.getString(R.string.progress) + "：" + bean.progress + "/" + bean.total)
        viewHolder.setText(R.id.tv_update_data, act.getString(R.string.update_date) + "：" + bean.lastUpdate)
        viewHolder.setText(R.id.tv_update_data, act.getString(R.string.remarks) + "：" + bean.remarks)
        viewHolder.getView<AppCompatTextView>(R.id.tv_update_data).visibility = if (StringUtils.isEmpty(bean.remarks) || bean.remarks.equals("")) View.GONE else View.VISIBLE

        viewHolder.setViewVisibility(R.id.tv_start_date, View.VISIBLE)
        viewHolder.setViewVisibility(R.id.tv_progress, View.VISIBLE)
        viewHolder.setViewVisibility(R.id.tv_update_data, View.VISIBLE)
        viewHolder.setViewVisibility(R.id.tv_remarks, View.GONE)
        viewHolder.setViewVisibility(R.id.listview, View.GONE)

        viewHolder.setOnItemClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                UIHelper.startAssetAct(bean);
            }
        })
    }

}