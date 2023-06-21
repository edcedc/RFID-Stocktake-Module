package com.yc.reid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.yc.reid.R
import com.yc.reid.weight.LinearDividerItemDecoration
import kotlinx.android.synthetic.main.b_not_title_recycler.recyclerView
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author
 * @Date 2023/6/9 10:59
 * @Description
 */
class InventoryDescAdapter (
    act: Context,
    jsonArray: JSONArray
) : RecyclerView.Adapter<ViewHolder>() {

    val act = act
    val jsonArray = jsonArray

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.i_inventory_desc_text, parent, false))
    }

    override fun getItemCount(): Int = jsonArray.length()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val opt = jsonArray.getJSONObject(position)
        viewHolder.setText(R.id.tv_text, opt.optString("title"))
        val list = opt.optJSONArray("list")
        var adapter = InventoryDesc2Adapter(act, list)
        viewHolder.getView<ListView>(R.id.recyclerView).adapter = adapter
        adapter?.notifyDataSetChanged()
    }

}