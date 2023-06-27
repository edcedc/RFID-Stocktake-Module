package com.yc.reid.base

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.classic.common.MultipleStatusView
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.yc.reid.R
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment

/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/9/16
 * Time: 18:03
 */
abstract class BaseFragment: SwipeBackFragment(){

    /**
     * 多种状态的 View 的切换
     */
    internal var mLayoutStatusView: MultipleStatusView? = null

    var rootView: View? = null

    protected var pagerNumber = 1//网络请求默认第一页

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = layoutInflater.inflate(this.getLayoutId(), null, false)
        this.rootView = rootView
        // 初始化参数
        var bundle = arguments
        if (bundle == null) {
            bundle = Bundle()
        }
        initParms(bundle)
        mLayoutStatusView = rootView.findViewById(R.id.multipleStatusView)
        return attachToSwipeBack(rootView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.rootView?.let { initView(it) }
    }

    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId():Int

    /**
     *  传参
     */
    abstract fun initParms(bundle: Bundle)

    /**
     * 初始化 View
     */
    abstract fun initView(rootView: View)

    override fun onStop() {
        super.onStop()
        hideLoading()
    }

    protected fun showToast(str: String) {
        activity?.runOnUiThread {
            ToastUtils.showShort(str)
        }
    }


    protected fun setTitle(title : String){
        setTitle(true, title, null, -1)
    }
    protected fun setTitle(title : String, rightTitle: String?){
        setTitle(true, title, rightTitle, -1)
    }
    protected fun setTitleCenter(title : String, rightTitle: String?){
        setTitle(false, title, rightTitle, -1)
    }


    private fun setTitle(isBack : Boolean, title : String, rightTitle : String?, rightImg : Int){
//        setSofia(false)
        val mAppCompatActivity = activity as AppCompatActivity
        val toolbar = rootView?.findViewById<Toolbar>(R.id.toolbar)
        val topTitle = rootView?.findViewById<AppCompatTextView>(R.id.top_title)
        val topRightFy = rootView?.findViewById<FrameLayout>(R.id.top_right_fy)
        val topRight = rootView?.findViewById<AppCompatTextView>(R.id.top_right)
        val topImg = rootView?.findViewById<AppCompatImageView>(R.id.top_img)
        mAppCompatActivity.setSupportActionBar(toolbar)
        mAppCompatActivity.supportActionBar?.setTitle("")
        if (isBack) {
            toolbar?.setNavigationOnClickListener { activity?.onBackPressed() }
        } else {
            toolbar?.navigationIcon = null
        }
//        topTitle?.setText(title)
        toolbar?.title = title
        if (!StringUtils.isEmpty(rightTitle)) {
            topRightFy?.setVisibility(View.VISIBLE)
            topRight?.setText(rightTitle)
            topImg?.setVisibility(View.GONE)
        } else if (rightImg != -1) {
            topRightFy?.setVisibility(View.VISIBLE)
            topImg?.setBackgroundResource(rightImg)
            topRight?.setVisibility(View.GONE)
        } else {

        }
        topRightFy?.setOnClickListener({ setOnRightClickListener() })
    }


    protected open fun setOnRightClickListener( ) {

    }

    protected fun setSofia(isFullScreen: Boolean) {
        if (!isFullScreen) {
            val toolbar = rootView?.findViewById<Toolbar>(R.id.toolbar)
            ImmersionBar.with(this).statusBarColor(R.color.red_FF5A60).titleBar(toolbar).statusBarDarkFont(false).keyboardEnable(true).init()
        } else {
            ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(false).init()
        }
    }

    private var dialog: ProgressDialog? = null
    private val handler_load = 0
    private val handler_hide = 1
    private val handler_loadData = 5

    fun showLoading() {
        dialogText = "Loading..."
        mHandler.sendEmptyMessage(handler_load)
    }
    fun showLoading1() {
        dialogText = "Saving Result to Local"
        mHandler.sendEmptyMessage(handler_load)
    }

    fun showUiLoading() {
        mHandler.sendEmptyMessage(handler_loadData)
    }

    open fun hideLoading() {
        mHandler.sendEmptyMessage(handler_hide)
    }

    fun errorText(text : String, errorCode : Int) {
        LogUtils.e(text, errorCode)
        showToast(text + "\n" + errorCode)
        hideLoading()
    }

    var dialogText: String? = "Loading..."
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                handler_load -> {
                    if (dialog != null && dialog!!.isShowing()) return
                    dialog = ProgressDialog(activity)
                    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog!!.setCanceledOnTouchOutside(false)
                    dialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    dialog!!.setMessage(dialogText)
                    dialog!!.show()
                }
                handler_hide -> {
                    if (dialog != null && dialog!!.isShowing()) {
                        dialog!!.dismiss()
                    }
                    if (mLayoutStatusView != null){
                        mLayoutStatusView?.showContent();
                    }
                }
                handler_loadData -> {
                    if (mLayoutStatusView != null){
                        mLayoutStatusView?.showLoading();
                    }
                }
            }
        }
    }

    protected fun setRecyclerViewType(recyclerView: RecyclerView) {
        recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setRecyclerView(recyclerView)
    }

    protected fun setRecyclerViewType(recyclerView: RecyclerView, baColor: Int) {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        setRecyclerView(recyclerView)
        activity?.let { ContextCompat.getColor(it, baColor) }?.let { recyclerView.setBackgroundColor(it) }
    }

    private fun setRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        //        recyclerView.setBackgroundColor(ContextCompat.getColor(act,baColor));
    }

    /**
     * 是否有更多
     */
    protected fun setRefreshLayoutMode(listSize: Int, totalRow: Int, refreshLayout: SmartRefreshLayout) {
        if (listSize == totalRow) {
            refreshLayout.setEnableLoadMore(false)
        } else {
            refreshLayout.setEnableLoadMore(true)
        }
    }

}