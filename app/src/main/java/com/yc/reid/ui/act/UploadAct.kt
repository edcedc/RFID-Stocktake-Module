package com.yc.reid.ui.act

import android.os.Bundle
import com.yc.reid.R
import com.yc.reid.base.BaseActivity
import com.yc.reid.ui.InventorySearchFrg
import com.yc.reid.ui.UploadFrg

/**
 * @Author nike
 * @Date 2023/6/13 14:49
 * @Description
 */
class UploadAct  : BaseActivity(){

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        val frg = UploadFrg::class.java.newInstance()
        var bundle = Bundle()
        frg.arguments = bundle
        if (findFragment(UploadFrg::class.java) == null) {
            loadRootFragment(R.id.fl_container, frg)
        }
    }

    override fun initParms(bundle: Bundle) {
    }
}