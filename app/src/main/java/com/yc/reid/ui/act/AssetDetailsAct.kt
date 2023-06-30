package com.yc.reid.ui.act

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.yc.reid.R
import com.yc.reid.adapter.MyPagerAdapter
import com.yc.reid.base.BaseActivity
import com.yc.reid.ui.AssetDetailsFrg
import com.yc.reid.ui.AssetSearchFrg
import com.yc.reid.ui.AssetUploadFrg
import com.yc.reid.weight.TabEntity
import kotlinx.android.synthetic.main.a_asset_details.tblayout
import kotlinx.android.synthetic.main.a_asset_details.viewPager
import org.json.JSONObject


/**
 * @Author nike
 * @Date 2023/6/8 19:29
 * @Description
 */
class AssetDetailsAct:BaseActivity() {

    val mFragments = ArrayList<Fragment>()

    var mTabEntities = ArrayList<CustomTabEntity>()

    var bean: String? = null

    override fun getLayoutId(): Int = R.layout.a_asset_details

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

        val searchFrg = AssetSearchFrg::class.java.newInstance()
        searchFrg.arguments = bundle
//        mFragments!!.add(searchFrg)

        val readFrg = AssetDetailsFrg::class.java.newInstance()
        readFrg.arguments = bundle
        mFragments!!.add(readFrg)

        val noFrg = AssetUploadFrg::class.java.newInstance()
        noFrg.arguments = bundle
        mFragments!!.add(noFrg)

        viewPager.setAdapter(MyPagerAdapter(this, mFragments))
        viewPager.offscreenPageLimit = mFragments.size
//        viewPager.currentItem = 1
        viewPager.isUserInputEnabled = false
    }

    override fun initParms(bundle: Bundle) {
        bean = bundle.getString("bean")
        setTitleText(bundle.getString("assetNo")!!)

    }

}