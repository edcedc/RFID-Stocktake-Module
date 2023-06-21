package com.yc.reid.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.yc.reid.R;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by 李可乐 on 2016/9/8 0008.
 */
public abstract class BaseBottomSheetFrg extends BottomSheetDialogFragment {
    protected Context act;

    protected View rootView;
    protected BottomSheetBehavior mBehavior;

    protected Dialog dialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.act = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除缓存View和当前ViewGroup的关联
        ((ViewGroup) (rootView.getParent())).removeView(rootView);
        hideLoading();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //每次打开都调用该方法 类似于onCreateView 用于返回一个Dialog实例
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        if (rootView == null) {
            getActivity().getWindow().setSoftInputMode
                    (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


            //缓存下来的View 当为空时才需要初始化 并缓存
            rootView = View.inflate(act, bindLayout(), null);
//            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ScreenUtils.getScreenHeight() / 3 * 2 ));
        }
        resetView();
        //设置View重新关联
        dialog.getWindow().getAttributes().windowAnimations = R.style.FragmentDialogAnimation;
        dialog.setContentView(rootView);
        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        mBehavior.setHideable(true);
        //圆角边的关键
        ((View) rootView.getParent()).setBackgroundColor(Color.TRANSPARENT);
        rootView.post(() -> {
            /**
             * PeekHeight默认高度256dp 会在该高度上悬浮
             * 设置等于view的高 就不会卡住
             */
            mBehavior.setPeekHeight(rootView.getHeight());
        });
        // 初始化参数
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        initParms(bundle);
        initView(rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return dialog;
    }

    protected abstract void initParms(Bundle bundle);

    public abstract int bindLayout();

    /**
     * 初始化View和设置数据等操作的方法
     */
    public abstract void initView(View view);

    /**
     * 重置的View和数据的空方法 子类可以选择实现
     * 为避免多次inflate 父类缓存rootView
     * 所以不会每次打开都调用{@link #initView(View)}方法
     * 但是每次都会调用该方法 给子类能够重置View和数据
     */
    public void resetView() {

    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    /**
     * 使用关闭弹框 是否使用动画可选
     * 使用动画 同时切换界面Aty会卡顿 建议直接关闭
     *
     * @param isAnimation
     */
    public void close(boolean isAnimation) {
        if (isAnimation) {
            if (mBehavior != null)
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            dismiss();
        }
    }


    public void onError(Throwable e) {
        mHandler.sendEmptyMessage(handler_hide);
        LogUtils.e(e.getMessage());
        ToastUtils.showShort(e.getMessage());
    }

    public void showLoading() {
        mHandler.sendEmptyMessage(handler_load);
    }

    public void hideLoading() {
        mHandler.sendEmptyMessage(handler_hide);
    }

    private CompositeDisposable compositeDisposable;

    public void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    private ProgressDialog progressDialog;

    private final int handler_load = 0;
    private final int handler_hide = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case handler_load:
                    if (progressDialog != null && progressDialog.isShowing()) return;
                    progressDialog = new ProgressDialog(act);
                    progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("请求网络中...");
                    progressDialog.show();
                    break;
                case handler_hide:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
            }
        }
    };

    protected void showToast(final String str) {
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
//                ToastUtils.showLong(str);
        ToastUtils.showShort(str);
    }

}