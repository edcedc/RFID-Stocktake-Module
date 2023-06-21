package com.yc.reid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import com.blankj.utilcode.util.StringUtils
import com.yc.reid.INVENTORY_FAIL
import com.yc.reid.R
import com.yc.reid.api.UIHelper
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.DataBean
import com.yc.reid.bean.sql.UploadStockDataSql
import java.util.Objects

/**
 * @Author nike
 * @Date 2023/6/13 15:00
 * @Description
 */
class UploadAdapter  (act: Context, root: BaseFragment, listBean: List<UploadStockDataSql>) : BaseRecyclerviewAdapter<UploadStockDataSql>
    (act, listBean as ArrayList<UploadStockDataSql>) {

    override fun onCreateViewHolde(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.i_upload, parent, false))
    }

    override fun onBindViewHolde(viewHolder: ViewHolder, position: Int) {
        val bean = listBean[position]
        viewHolder.setText(R.id.tv_text, "Stock Take：" + bean.orderNo + " | " + bean.time)
        viewHolder.setText(R.id.btn_commit, if (bean.isSave == 0) act.getText(R.string.click_upload) else act.getText(R.string.click_upload1))
        if (bean.isSave == 0){
            viewHolder.setOnItemClickListener {
                onItemClickListener?.onItemClick(bean as Object, position)
            }
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

}