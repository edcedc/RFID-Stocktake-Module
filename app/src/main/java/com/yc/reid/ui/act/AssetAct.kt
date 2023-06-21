package com.yc.reid.ui.act

import android.os.Bundle
import com.yc.reid.R
import com.yc.reid.base.BaseActivity
import com.yc.reid.ui.AssetFrg

/**
 * @Author nike
 * @Date 2023/6/2 14:08
 * @Description
 */
class AssetAct: BaseActivity(){

    var bean :String? = null
    override fun getLayoutId(): Int  = R.layout.activity_main

    override fun initView() {
        val frg = AssetFrg::class.java.newInstance()
        var bundle = Bundle()
        bundle.putString("bean", bean)
        frg.arguments = bundle
        if (findFragment(AssetFrg::class.java) == null) {
            loadRootFragment(R.id.fl_container, frg)
        }
    }

    override fun initParms(bundle: Bundle) {
        bean =  bundle.getString("bean");
    }

}