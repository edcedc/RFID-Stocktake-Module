package com.yc.reid.ui.act

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.yc.reid.R
import com.yc.reid.base.BaseActivity
import com.yc.reid.mvp.impl.HtmlContract
import com.yc.reid.mvp.presenter.HtmlPresenter
import kotlinx.android.synthetic.main.a_html.*

/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/9/23
 * Time: 19:27
 */
class HtmlAct : BaseActivity(), HtmlContract.View {


    var type: Int = 0

    var url: String? = null
//    var title: String? = null

    companion object {

        val USER_AGREEMENT = 1//用户协议
        val DESC = 2//查看详情
        val ADV = 3//平台广告
        val PROBLEMS = 4//问题详情

    }

    private val mPresenter by lazy { HtmlPresenter() }


    override fun getLayoutId(): Int = R.layout.a_html

    override fun initParms(bundle: Bundle) {
        type = bundle.getInt("type")
        url = bundle.getString("url")
        title = bundle.getString("title")
    }

    override fun initView() {
        setSofia(false)
        mPresenter.init(this)
        when (type) {
            USER_AGREEMENT -> {
                mPresenter.onUrl(type)
            }
            DESC ->{
                webView.loadUrl(url)
            }
            ADV ->{
                webView.loadUrl(url)
            }
            PROBLEMS ->{
                setUrl("问题详情", url)
            }
        }
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                LogUtils.e(url)
                return true
            }

            override fun onReceivedError(var1: WebView, var2: Int, var3: String, var4: String) {
                progressBar.setVisibility(View.GONE)
                ToastUtils.showShort("网页加载失败")
            }
        })
        //进度条
        webView.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE)
                    return
                }
                progressBar.setVisibility(View.VISIBLE)
                progressBar.setProgress(newProgress)
            }
        })
    }

    override fun setUrl(title: String?, content: String?) {
        webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null)
    }

}