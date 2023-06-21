package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.yc.reid.R
import com.yc.reid.base.BaseFragment
import com.yc.reid.event.InventorySearchEvent
import com.yc.reid.event.InventorySearchIdEvent
import kotlinx.android.synthetic.main.f_asset_search.bt_sure
import kotlinx.android.synthetic.main.f_asset_search.progress_bar
import kotlinx.android.synthetic.main.f_asset_search.tv_text
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


/**
 * @Author nike
 * @Date 2023/6/8 19:40
 * @Description 上传
 */
class AssetSearchFrg:BaseFragment(), OnClickListener {

    var LabelTag: String? = null

    var isState: Boolean? = false

    override fun getLayoutId(): Int = R.layout.f_asset_search

    override fun initParms(bundle: Bundle) {
        var bean = JSONObject(bundle.getString("bean"))
        LabelTag = bean.optString("LabelTag")
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
//        layout.visibility = View.VISIBLE
    }

    override fun initView(rootView: View) {
        bt_sure.setOnClickListener(this)
        EventBus.getDefault().register(this)


    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.bt_sure ->{
                if (!isState!!){
                    start()
                }else{
                    stop()
                }
            }
        }
    }

    private fun stop() {
        bt_sure.setText(getText(R.string.start))
        isState = false
        EventBus.getDefault().post(InventorySearchIdEvent(LabelTag!!, isState!!))
    }

    private fun start() {
        bt_sure.setText(getText(R.string.stop))
        isState = true
        EventBus.getDefault().post(InventorySearchIdEvent(LabelTag!!, isState!!))
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().post(InventorySearchIdEvent(LabelTag!!, false))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInventorySearchEvent(event: InventorySearchEvent){
        if (!event.LabelTag.equals(LabelTag))return
        val replace = event.Rssi.replace("-", "")
        val i = Math.round(replace.toDouble()).toInt()
        tv_text.text = i.toString()
        progress_bar.progress = i
    }

}