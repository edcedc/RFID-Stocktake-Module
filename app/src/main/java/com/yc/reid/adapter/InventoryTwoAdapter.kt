package com.yc.reid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ListView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.yc.reid.INVENTORY_ALL
import com.yc.reid.INVENTORY_FAIL
import com.yc.reid.INVENTORY_NOT
import com.yc.reid.INVENTORY_READ
import com.yc.reid.R
import com.yc.reid.SCAN_STATUS_MANUALLY
import com.yc.reid.SCAN_STATUS_QRCODE
import com.yc.reid.SCAN_STATUS_SCAN
import com.yc.reid.api.UIHelper
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StockChildSql
import org.json.JSONObject


class InventoryTwoAdapter(
    act: Context,
    root: BaseFragment,
    listBean: List<StockChildSql>
) : BaseRecyclerviewAdapter<StockChildSql>(act, listBean as ArrayList<StockChildSql>), Filterable {


    override fun onCreateViewHolde(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.i_inventory, parent, false)
        )
    }

    override fun onBindViewHolde(viewHolder: ViewHolder, position: Int) {
        val bean = mFilterList[position]
        viewHolder.setText(R.id.tv_end_date, act.getString(R.string.label) + "：" + bean.LabelTag)
//        viewHolder.setText(R.id.tv_progress, act.getString(R.string.user) + "：" + bean.CreateUser)
//        viewHolder.setText(R.id.tv_update_data, act.getString(R.string.start_date) + "：" + bean.CreateDate)
//        viewHolder.getView<AppCompatTextView>(R.id.tv_progress).visibility = View.GONE
//        viewHolder.getView<AppCompatTextView>(R.id.tv_update_data).visibility = View.GONE
//        viewHolder.getView<AppCompatTextView>(R.id.tv_remarks).visibility = if (StringUtils.isEmpty(bean.remarks) || bean.remarks.equals("")) View.GONE else View.VISIBLE
//        viewHolder.setText(R.id.tv_remarks, act.getString(R.string.remarks) + "：" + bean.remarks)
        val listStr = ArrayList<String>()
        val tag = bean.Tag
        if (!StringUtils.isEmpty(tag)) {
            val jsonObject = JSONObject(tag)
            val headerkeys: Iterator<String> = jsonObject.keys()
            while (headerkeys.hasNext()) {
                val headerkey = headerkeys.next()
                val headerValue: String = jsonObject.getString(headerkey)
                listStr.add(headerkey + "：" + headerValue)
            }
        }
        viewHolder.getView<ListView>(R.id.listview).setClickable(false);
        viewHolder.getView<ListView>(R.id.listview).setPressed(false);
        viewHolder.getView<ListView>(R.id.listview).adapter = InventoryListTextAdapter(act, listStr)

        viewHolder.getView<AppCompatTextView>(R.id.tv_start_date).visibility = View.VISIBLE
        when (bean.scan_status) {
            SCAN_STATUS_QRCODE -> {
                viewHolder.setText(R.id.tv_start_date, "Scan status：QRCode")
            }
            SCAN_STATUS_SCAN -> {
                viewHolder.setText(R.id.tv_start_date, "Scan status：RFID")
            }
            SCAN_STATUS_MANUALLY -> {
                viewHolder.setText(R.id.tv_start_date, "Scan status：Manually")
            }
            else -> {
                viewHolder.setText(R.id.tv_start_date, "Scan status：")
            }
        }

        when (bean.type) {
            INVENTORY_ALL -> {
                viewHolder.setText(R.id.tv_title, bean.AssetNo)
                viewHolder.getView<AppCompatImageView>(R.id.iv_state).visibility = View.VISIBLE
                viewHolder.getView<AppCompatImageView>(R.id.iv_state).background =
                    if (bean.isVisibility) ContextCompat.getDrawable(
                        act,
                        R.mipmap.icon_25
                    ) else ContextCompat.getDrawable(act, R.mipmap.icon_24)
            }

            INVENTORY_READ -> {
                viewHolder.setText(
                    R.id.tv_title,
                    bean.AssetNo + " | " + act.getString(R.string.in_stock)
                )
                viewHolder.getView<AppCompatImageView>(R.id.iv_state).visibility = View.VISIBLE
                viewHolder.getView<AppCompatImageView>(R.id.iv_state).background =
                    ContextCompat.getDrawable(act, R.mipmap.icon_25)
            }

            INVENTORY_NOT -> {
                viewHolder.setText(
                    R.id.tv_title,
                    bean.AssetNo + " | " + act.getString(R.string.not_library)
                )
                viewHolder.getView<AppCompatImageView>(R.id.iv_state).visibility = View.GONE
            }

            INVENTORY_FAIL -> {
                viewHolder.setText(R.id.tv_title, act.getString(R.string.abnormal))
                viewHolder.getView<AppCompatImageView>(R.id.iv_state).visibility = View.GONE
            }
        }

        viewHolder.setOnItemClickListener {
            if (bean.type == INVENTORY_FAIL) return@setOnItemClickListener
//            if (StringUtils.isEmpty(bean.LabelTag))return@setOnItemClickListener
            UIHelper.startInventoryDescAct(bean)
        }
        viewHolder.getView<ListView>(R.id.listview)
            .setOnItemClickListener { adapterView, view, i, l ->
                if (bean.type == INVENTORY_FAIL) return@setOnItemClickListener
//            if (StringUtils.isEmpty(bean.LabelTag))return@setOnItemClickListener
                UIHelper.startInventoryDescAct(bean)
            }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    var mFilterList = ArrayList<StockChildSql>()

    fun appendList(list: List<StockChildSql>) {
        listBean = list
        //这里需要初始化filterList
        mFilterList = list as ArrayList<StockChildSql>
    }

    override fun getItemCount(): Int {
        return mFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = listBean as ArrayList<StockChildSql>
                } else {
                    val filteredList: MutableList<StockChildSql> = ArrayList()

                    for (i in listBean.indices) {
                        val bean = listBean[i]
                        val labelTag = bean.LabelTag
                        if (!StringUtils.isEmpty(labelTag)) {
                            if (labelTag!!.contains(charString)) {
                                filteredList.add(bean)
                            }
                        }
                    }
                    mFilterList = filteredList as ArrayList<StockChildSql>
                }
                val filterResults = FilterResults()
                filterResults.values = mFilterList
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mFilterList = filterResults.values as ArrayList<StockChildSql>
                notifyDataSetChanged()
            }
        }
    }

}