package com.yc.reid.ui.act

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.yc.reid.R
import com.yc.reid.adapter.MyPagerAdapter
import com.yc.reid.base.BaseActivity
import com.yc.reid.ui.InventoryDescFrg
import com.yc.reid.ui.InventoryDescSearchFrg
import com.yc.reid.ui.InventoryDescUploadFrg
import com.yc.reid.weight.TabEntity
import kotlinx.android.synthetic.main.f_inventory_search.tblayout
import kotlinx.android.synthetic.main.f_inventory_search.viewPager
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject


/**
 * @Author nike
 * @Date 2023/6/8 19:29
 * @Description
 */
class InventoryDescAct:BaseActivity() {

    val mFragments = ArrayList<Fragment>()

    var mTabEntities = ArrayList<CustomTabEntity>()

    var bean: String? = null

    var ids: String? = null

    var assetNo: String? = null

    override fun getLayoutId(): Int = R.layout.a_inventory_desc

    override fun initView() {
        var mTitles =
            arrayOf(
//                getString(R.string.search_assets),
                getString(R.string.detailed),
                getString(R.string.remarks))

        for (i in mTitles.indices) {
            mTabEntities.add(TabEntity(mTitles[i]))
        }

        tblayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                viewPager.currentItem = position
            }
            override fun onTabReselect(position: Int) {

            }
        })
        tblayout.setTabData(mTabEntities)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tblayout.setCurrentTab(position)
            }
        })

        var bundle = Bundle()
        bundle.putString("bean", bean!!)
        bundle.putString("assetNo", assetNo)
        bundle.putString("ids", ids)

        val searchFrg = InventoryDescSearchFrg::class.java.newInstance()
        searchFrg.arguments = bundle
//        mFragments!!.add(searchFrg)

        val readFrg = InventoryDescFrg::class.java.newInstance()
        readFrg.arguments = bundle
        mFragments!!.add(readFrg)

        val noFrg = InventoryDescUploadFrg::class.java.newInstance()
        noFrg.arguments = bundle
        mFragments!!.add(noFrg)

        viewPager.setAdapter(MyPagerAdapter(this, mFragments))
        viewPager.offscreenPageLimit = mFragments.size
//        viewPager.currentItem = 1
        viewPager.isUserInputEnabled = false
    }

    override fun initParms(bundle: Bundle) {
        bean = bundle.getString("bean")
        assetNo = bundle.getString("assetNo")
        ids = bundle.getString("ids")
        if (!StringUtils.isEmpty(bean)){
            val json = JSONObject(bean)
            setTitle(json.optString("AssetNo"))
        }
    }

}