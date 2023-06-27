package com.yc.reid.ui

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.gson.Gson
import com.yc.reid.INVENTORY_FAIL
import com.yc.reid.INVENTORY_READ
import com.yc.reid.R
import com.yc.reid.adapter.MyPagerAdapter
import com.yc.reid.api.UIHelper
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.DataBean
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.event.SearchListEvent
import com.yc.reid.event.StartReceivingEvent
import com.yc.reid.event.StockTakeEvent
import com.yc.reid.event.StockTakeListEvent
import com.yc.reid.event.StockTakeUpdateTtitleEvent
import com.yc.reid.mvp.impl.InventoryTwoContract
import com.yc.reid.mvp.presenter.InventoryTwoPresenter
import com.yc.reid.utils.MusicUtils
import com.yc.reid.weight.EditTextWithDrawable
import com.yc.reid.weight.TabEntity
import kotlinx.android.synthetic.main.f_inventory_search.bt_seng
import kotlinx.android.synthetic.main.f_inventory_search.bt_storage
import kotlinx.android.synthetic.main.f_inventory_search.et_text
import kotlinx.android.synthetic.main.f_inventory_search.iv_qr
import kotlinx.android.synthetic.main.f_inventory_search.tblayout
import kotlinx.android.synthetic.main.f_inventory_search.viewPager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import org.litepal.LitePal


/**
 * @Author nike
 * @Date 2023/6/2 14:11
 * @Description 盘点清单二级
 */
class InventorySearchFrg : BaseFragment(), OnClickListener, InventoryTwoContract.View{

    val mFragments = ArrayList<Fragment>()

    var mTabEntities = ArrayList<CustomTabEntity>()

    var mPosition: Int = 0

    val mPresenter by lazy { InventoryTwoPresenter() }

    var listBean = ArrayList<StockChildSql>()

    var isPlay = false

    var stocktakeno: String? = null

    var title1: String? = null

    override fun getLayoutId(): Int = R.layout.f_inventory_search

    override fun initParms(bundle: Bundle) {
        var bean = Gson().fromJson(bundle.getString("bean"), DataBean::class.java)
        this.stocktakeno = bean.stocktakeno
        this.title1 = bean.name
    }

    override fun initView(rootView: View) {
        setTitle(title1!!)
        //init sound
        mPresenter.init(this, activity)
        bt_seng.setOnClickListener(this)
        bt_storage.setOnClickListener(this)
        iv_qr.setOnClickListener(this)
        EventBus.getDefault().register(this)
        showUiLoading()
        mPresenter.onRequest(pagerNumber, stocktakeno!!)
        et_text.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                EventBus.getDefault().post(SearchListEvent(p0.toString()))
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        et_text.setOnRightDrawableClickListener(object : EditTextWithDrawable.onDrawableRightClick {
            override fun onClick() {
                showToast("搜索")
            }
        })

        var mTitles =
            arrayOf(
                getString(R.string.all),
                getString(R.string.already_read),
                getString(R.string.not_library),
                getString(R.string.abnormal)
            )

        for (i in mTitles.indices) {
            mTabEntities.add(TabEntity(mTitles[i]))
        }

        tblayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                viewPager.setCurrentItem(position)
            }

            override fun onTabReselect(position: Int) {

            }
        })
        tblayout.setTabData(mTabEntities)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tblayout.setCurrentTab(position)
                mPosition = position
            }
        })
        viewPager.currentItem = 0
        viewPager.isUserInputEnabled = false
        setSwipeBackEnable(false)
    }

    override fun onBackPressedSupport(): Boolean {
        if (mPosition != 0) {
            viewPager.currentItem = 0
            return true
        } else {
            return false
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_storage -> {
                mPresenter.onUpload(stocktakeno)
            }
            R.id.bt_seng -> {
                if (isPlay) {
                    stop()
                } else {
                    isPlay = true
                    bt_seng.text = getString(R.string.stop)
                    MusicUtils.init(activity)
                    EventBus.getDefault().post(StartReceivingEvent(true))
                }
            }
            R.id.iv_qr ->{
                UIHelper.startZxingAct(stocktakeno)
            }
        }
    }

    private fun stop() {
        isPlay = false
        bt_seng.text = getString(R.string.start)
        MusicUtils.clear()
        EventBus.getDefault().post(StartReceivingEvent(false))
    }

    /**
     *  标题加数量
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStockTakeUpdateTtitleEvent(event: StockTakeUpdateTtitleEvent) {
        setTitle(title1!! + "(" + event!!.size + ")")
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicUtils.clear()
        EventBus.getDefault().unregister(this)
    }

    override fun setRefreshLayoutMode(totalRow: Int) {
        TODO("Not yet implemented")
    }

    override fun setData(objects: Object) {
        TODO("Not yet implemented")
    }

    override fun setData(list: ArrayList<StockChildSql>, bean: JSONObject) {
        listBean.addAll(list)

        var bundle = Bundle()
        bundle.putString("stocktakeno", stocktakeno!!)

        val allFrg = InventoryAllFrg::class.java.newInstance()
        allFrg.arguments = bundle
        mFragments!!.add(allFrg)

        val readFrg = InventoryReadFrg::class.java.newInstance()
        readFrg.arguments = bundle
        mFragments!!.add(readFrg)

        val noFrg = InventoryNoFrg::class.java.newInstance()
        noFrg.arguments = bundle
        mFragments!!.add(noFrg)

        val failFrg = InventoryFailFrg::class.java.newInstance()
        failFrg.arguments = bundle
        mFragments!!.add(failFrg)

        viewPager.setAdapter(MyPagerAdapter(requireActivity(), mFragments))
        viewPager.offscreenPageLimit = mFragments.size

        //加载卡顿，延迟一下
        Handler().postDelayed({
            hideLoading()
        }, 1000)
    }

    /**
     *  存储扫描的EPC
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStockTakeListEvent(event: StockTakeListEvent) {
        var isSeach = false
        listBean.forEachIndexed() { index, dataBean ->
            //存在的
            if ((dataBean.LabelTag != null && StringUtils.equalsIgnoreCase(dataBean.LabelTag, event.LabelTag))
                || (StringUtils.equalsIgnoreCase(dataBean.AssetNo, event.LabelTag))) {
                var labelId: String? = event.LabelTag
                if (StringUtils.isEmpty(dataBean.LabelTag)){
                    labelId = dataBean.AssetNo
                }
                var stockChildSql = LitePal.where("LabelTag = ? and OrderNo = ?", labelId, stocktakeno).findFirst(StockChildSql::class.java)
                if (stockChildSql == null){
                    stockChildSql = LitePal.where("AssetNo = ? and OrderNo = ?", labelId, stocktakeno).findFirst(StockChildSql::class.java)
                }
                stockChildSql.ids = stocktakeno + dataBean.AssetNo
                stockChildSql.type = INVENTORY_READ
                stockChildSql.roNo = LitePal.findFirst(UserDataSql::class.java).RoNo
                stockChildSql.companyid = LitePal.findFirst(ConfigDataSql::class.java).companyid
                stockChildSql.scan_time = TimeUtils.getNowString()
                stockChildSql.scan_status = event.FoundStatus
                stockChildSql.save()
                isSeach = true
                EventBus.getDefault().post(StockTakeEvent(stockChildSql))
                MusicUtils.play()
            } else {
                //异常
//                setFailData(event)
            }
        }
        //异常
        if (!isSeach){
            setFailData(event)
        }

    }

    private fun setFailData(event: StockTakeListEvent) {
        val stockChildSql = LitePal.where("LabelTag = ? and OrderNo = ?", event.LabelTag, stocktakeno).findFirst(StockChildSql::class.java)
        if (stockChildSql == null) {
            val bean = StockChildSql()
            bean.ids = stocktakeno + event.LabelTag
            bean.OrderNo = stocktakeno
            bean.LabelTag = event.LabelTag
            bean.Rssi = event.Rssi
            bean.type = INVENTORY_FAIL
            bean.roNo = LitePal.findFirst(UserDataSql::class.java).RoNo
            bean.companyid = LitePal.findFirst(ConfigDataSql::class.java).companyid
            bean.scan_time = TimeUtils.getNowString()
            bean.scan_status = event.FoundStatus
            bean.save()
            EventBus.getDefault().post(StockTakeEvent(bean))
            MusicUtils.play()
        }
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stop()
    }

}