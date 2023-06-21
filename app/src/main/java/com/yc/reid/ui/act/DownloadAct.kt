package com.yc.reid.ui.act

import android.os.Bundle
import com.yc.reid.R
import com.yc.reid.base.BaseActivity
import com.yc.reid.ui.DownloadFrg
import com.yc.reid.ui.InventorySearchFrg
import com.yc.reid.ui.UploadFrg

/**
 * @Author nike
 * @Date 2023/6/13 14:49
 * @Description
 */
class DownloadAct  : BaseActivity(){

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        val frg = DownloadFrg::class.java.newInstance()
        var bundle = Bundle()
        frg.arguments = bundle
        if (findFragment(DownloadFrg::class.java) == null) {
            loadRootFragment(R.id.fl_container, frg)
        }
    }

    override fun initParms(bundle: Bundle) {
    }
}