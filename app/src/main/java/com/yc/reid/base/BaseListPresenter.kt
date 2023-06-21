package com.yc.reid.base

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


/**
 * Created by xuhao on 2017/11/16.
 *
 */
open class BaseListPresenter<T : IBaseListView> : IListPresenter<T> {

    var mRootView: T? = null

    var act: Activity? = null

    private var compositeDisposable = CompositeDisposable()

    override fun init(mRootView: T, activity: Activity?) {
        this.mRootView = mRootView
        this.act = activity
    }

    override fun init(mRootView: T) {
        this.mRootView = mRootView
    }

    override fun detachView() {
        mRootView = null

         //保证activity结束时取消所有正在执行的订阅
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }

    }

    private val isViewAttached: Boolean
        get() = mRootView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")

    protected fun showToast(s: String) {
        ToastUtils.showShort(s)
    }

    protected fun toRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

}