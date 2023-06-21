package com.yc.reid.adapter

import android.view.View

/**
 *
 * Description: Adapter条目的长按事件
 */
interface OnItemLongClickListener {

    fun onItemLongClick(obj: Any?, position: Int, v: View): Boolean

}
