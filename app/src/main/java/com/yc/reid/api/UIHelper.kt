package com.yc.reid.api

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.google.gson.Gson
import com.yc.reid.MainActivity
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.bean.sql.StocktakeListSql
import com.yc.reid.ui.act.AssetAct
import com.yc.reid.ui.act.InventorySearchAct
import com.yc.reid.ui.SetFrg
import com.yc.reid.ui.UploadFrg
import com.yc.reid.ui.act.*


/**
 * Created by Administrator on 2017/2/22.
 */

class UIHelper private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        fun startMainAct() {
            ActivityUtils.startActivity(MainActivity::class.java)
        }

        /**
         *  登录
         */
        fun startLoginAct() {
            ActivityUtils.startActivity(LoginAct::class.java)
        }

        /**
         *  上传
         */
        fun startUploadAct() {
            ActivityUtils.startActivity(UploadAct::class.java)
        }

        /**
         *  下载
         */
        fun startDownloadAct() {
            ActivityUtils.startActivity(DownloadAct::class.java)
        }

        /**
         *  扫描
         */
        fun startZxingAct(stocktakeno: String?) {
            var bundle = Bundle()
            bundle.putString("stocktakeno", stocktakeno)
            ActivityUtils.startActivity(bundle, ZxingAct::class.java)
        }

        /**
         *  盘点订单
         */
        fun startAssetAct(bean: StocktakeListSql) {
            var bundle = Bundle()
            bundle.putString("bean", Gson().toJson(bean))
            ActivityUtils.startActivity(bundle, AssetAct::class.java)

        }

          /**
         *  盘点详情
         */
        fun startInventoryDescAct(bean: StockChildSql) {
            var bundle = Bundle()
            bundle.putString("bean", Gson().toJson(bean))
            ActivityUtils.startActivity(bundle, InventoryDescAct::class.java)

        }

        /**
         *  设置
         */
        fun startSetFrg(root: BaseFragment) {
            val frg = SetFrg()
            val bundle = Bundle()
            frg.setArguments(bundle)
            root.start(frg)
        }

        /**
         *  上传
         */
        fun startUploadFrg(root: BaseFragment) {
            val frg = UploadFrg()
            val bundle = Bundle()
            frg.setArguments(bundle)
            root.start(frg)
        }

        /**
         *  盘点清单详情
         */
        fun startAssetDetailsAct(bean: StockChildSql) {
            var bundle = Bundle()
            bundle.putString("bean", Gson().toJson(bean))
            ActivityUtils.startActivity(bundle, AssetDetailsAct::class.java)


        }

        /**
         *  盘点清单二级类目
         */
        fun startInventorySearchAct(bean: StocktakeListSql) {
            var bundle = Bundle()
            bundle.putString("bean", Gson().toJson(bean))
            ActivityUtils.startActivity(bundle, InventorySearchAct::class.java)

        }

        /**
         *  各种H5
         */
        fun startHtmlAct(type: Int) {
            val bundle = Bundle()
            bundle.putInt("type", type)
            ActivityUtils.startActivity(bundle, HtmlAct::class.java)
        }

        fun startHtmlAct(type: Int, url: String?) {
            val bundle = Bundle()
            bundle.putInt("type", type)
            bundle.putString("url", url)
            ActivityUtils.startActivity(bundle, HtmlAct::class.java)
        }

        fun startHtmlAct(type: Int, url: String?, title: String?) {
            val bundle = Bundle()
            bundle.putInt("type", type)
            bundle.putString("url", url)
            bundle.putString("title", title)
            ActivityUtils.startActivity(bundle, HtmlAct::class.java)
        }

        /**
         *  视频页面
         */
        fun startVideoAct(video: String, image: String) {
            var bundle = Bundle()
            bundle.putString("video", video)
            bundle.putString("image", image)
            //            ActivityUtils.startActivity(bundle, VideoAct::class.java)
        }

    }
}

