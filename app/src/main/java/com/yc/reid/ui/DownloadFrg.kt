package com.yc.reid.ui

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.blankj.utilcode.util.LogUtils
import com.yc.reid.R
import com.yc.reid.api.UIHelper
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.bean.sql.UploadStockDataSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.event.DownloadStateEvent
import com.yc.reid.mvp.impl.DownloadContract
import com.yc.reid.mvp.presenter.DownloadPresenter
import com.yc.reid.utils.PopupWindowTool
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.f_download.progress_bar
import kotlinx.android.synthetic.main.f_download.tv_text
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import org.litepal.LitePal.findFirst
import java.text.NumberFormat
import java.util.concurrent.TimeUnit


/**
 * @Author nike
 * @Date 2023/6/13 10:30
 * @Description 上传
 */
class DownloadFrg : BaseFragment(), DownloadContract.View, OnClickListener {

    val mPresenter by lazy { DownloadPresenter() }

    val numberFormat = NumberFormat.getInstance()

    var mDisposable = CompositeDisposable()

    override fun getLayoutId(): Int = R.layout.f_download

    override fun initParms(bundle: Bundle) {}

    override fun initView(rootView: View) {
        setTitle(getString(R.string.download))
        mPresenter.init(this, activity)
        numberFormat.setMaximumFractionDigits(2)
//        showUiLoading()
        tv_text.setOnClickListener(this)
    }

    override fun setProgress(count: Int, size: Int) {

        Observable.interval(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long?> {
                override fun onSubscribe(d: Disposable) {
                    mDisposable.add(d)
                }
                override fun onNext(t: Long) {
                    val stocktakeListSql = LitePal.where("roNo = ?", findFirst(UserDataSql::class.java).RoNo).find(StockChildSql::class.java)
                    var result = numberFormat.format(stocktakeListSql.size.toFloat() / count.toFloat() * 100).toFloat()
                    LogUtils.e(result, stocktakeListSql.size, count)
                    if (result < 1){
                        result = 1F
                    }else if (result > 100){
                        result = 100F
                    }
                    progress_bar.setProgress(result)
                    progress_bar.setText("${result.toInt()}%")

                    if (stocktakeListSql.size >= count){
                        tv_text.text = getText(R.string.download_complete)
                        closeTimer()
                    }
                }
                override fun onError(e: Throwable) {
                    LogUtils.e(e.message)
                }
                override fun onComplete() {

                }
            })

//        var result = numberFormat.format(size.toFloat() / count.toFloat() * 100).toFloat()
//        if (result < 1){
//            result = 1F
//        }else if (result > 100){
//            result = 100F
//        }
//        runOnUiThread(){
//            progress_bar.postDelayed({
//                progress_bar.setText("${result.toInt()}%")
//                progress_bar.setProgress(result)
//            }, 1000)
//        }

//        val userDataSql = findFirst(UserDataSql::class.java)
//        val stocktakeListSql = LitePal.where("roNo = ?", userDataSql.RoNo).find(
//            StockChildSql::class.java)
//        if (stocktakeListSql.size == count){
//            tv_text.postDelayed({
//                tv_text.text = getText(R.string.download_complete)
//            }, 200)
//        }
    }

    var compositeDisposable: Disposable? = null
    override fun addCompositeDisposable(compositeDisposable: Disposable?) {
        this.compositeDisposable = compositeDisposable
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.dispose() // 取消订阅
        compositeDisposable = null
    }

    override fun onResume() {
        super.onResume()
        //判断是否有未上传的数据
        var uploadStockDataSqlList = LitePal.where("roNo = ? and isSave = 0", LitePal.findFirst(UserDataSql::class.java).RoNo).find(UploadStockDataSql::class.java)
        if (uploadStockDataSqlList != null && uploadStockDataSqlList.size != 0){
            setUploadHiht()
        }else{
            mPresenter.onRequest(pagerNumber)
        }
    }

    fun setUploadHiht() {
        PopupWindowTool.showDialog(activity).asConfirm(
            getText(R.string.remind), getText(R.string.remind1),
            getText(R.string.cancel), getText(R.string.confirm),{
                UIHelper.startUploadAct()
            }, {
                mPresenter.onRequest(pagerNumber)
            } , false
        ).show()
    }

    override fun setRefreshLayoutMode(totalRow: Int) {}

    override fun setData(objects: Object) {
        EventBus.getDefault().post(DownloadStateEvent(true))
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_text ->{
                val userDataSql = findFirst(UserDataSql::class.java)
                val stocktakeListSql = LitePal.where("roNo = ?", userDataSql.RoNo).find(
                    StockChildSql::class.java)
                LogUtils.e(stocktakeListSql.size)

                showToast("当前数据库查询到：" + stocktakeListSql.size)
//                mPresenter.onRequest(pagerNumber)
            }
        }
    }

    /**
     * 关闭定时器
     */
    fun closeTimer() {
        if (mDisposable != null){
            mDisposable?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        closeTimer()
    }

}

