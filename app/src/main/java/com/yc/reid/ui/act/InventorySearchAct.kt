package com.yc.reid.ui.act

import android.os.Bundle
import com.yc.reid.R
import com.yc.reid.base.BaseActivity
import com.yc.reid.ui.InventorySearchFrg
import com.yc.reid.utils.MusicUtils

/**
 * @Author nike
 * @Date 2023/6/2 14:08
 * @Description
 */
class InventorySearchAct: BaseActivity(){

    var bean :String? = null

    override fun getLayoutId(): Int  = R.layout.activity_main

    override fun initView() {
        val frg = InventorySearchFrg::class.java.newInstance()
        var bundle = Bundle()
        bundle.putString("bean", bean)
        frg.arguments = bundle
        if (findFragment(InventorySearchFrg::class.java) == null) {
            loadRootFragment(R.id.fl_container, frg)
        }
    }

    override fun initParms(bundle: Bundle) {
        bean =  bundle.getString("bean");
    }

}