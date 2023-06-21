package com.yc.reid.ui.bottom;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatEditText;
import com.blankj.utilcode.util.StringUtils;
import com.yc.reid.R;
import com.yc.reid.base.BaseBottomSheetFrg;


/**
 * 作者：yc on 2018/8/4.
 * 邮箱：501807647@qq.com
 * 版本：v1.0
 *  打开相册或相机
 */
@SuppressLint("ValidFragment")
public class CameraBottomFrg extends BaseBottomSheetFrg implements View.OnClickListener{


    private int type;
    private AppCompatEditText et_url;
    private TextView tv_camera;
    private TextView tv_photo;
    private TextView tv_save;
    private TextView tv_url;
    private View ly_url;

    public CameraBottomFrg() {
    }

    public CameraBottomFrg(int type) {
        this.type = type;
    }

    @Override
    protected void initParms(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.p_camera;
    }

    @Override
    public void initView(View view) {
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        view.findViewById(R.id.tv_url).setOnClickListener(this);
        tv_camera = view.findViewById(R.id.tv_camera);
        tv_photo = view.findViewById(R.id.tv_photo);
        tv_url = view.findViewById(R.id.tv_url);
        tv_save = view.findViewById(R.id.tv_save);
        ly_url = view.findViewById(R.id.ly_url);
        tv_camera.setOnClickListener(this);
        tv_photo.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        view.findViewById(R.id.layout).setOnClickListener(this);
        view.findViewById(R.id.tv_sure_url).setOnClickListener(this);
        et_url = view.findViewById(R.id.et_url);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tv_camera.setVisibility(View.VISIBLE);
        tv_photo.setVisibility(View.VISIBLE);
        tv_url.setVisibility(View.VISIBLE);
        ly_url.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cancel:
            case R.id.layout:
                dismiss();
                break;
             case R.id.tv_camera:
                if (listener != null){
                    listener.camera();
                    dismiss();
                }
                break;
             case R.id.tv_photo:
                 if (listener != null){
                     listener.photo();
                     dismiss();
                 }
                break;
            case R.id.tv_save:
                if (listener != null){
                    listener.save();
                    dismiss();
                }
                break;
            case R.id.tv_url:
                tv_camera.setVisibility(View.GONE);
                tv_photo.setVisibility(View.GONE);
                tv_url.setVisibility(View.GONE);
                ly_url.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_sure_url:
                if (listener != null){
                    String s = et_url.getText().toString();
                    if (StringUtils.isEmpty(s)){
                        showToast("地址不能为空");
                        return;
                    }
                    listener.url(s);
                    dismiss();
                }
                break;
        }
    }

    private onCameraListener listener;
    public void setCameraListener(onCameraListener listener){
        this.listener = listener;
    }

    public interface onCameraListener{
        void camera();
        void photo();
        void save();
        void url(String url);
    }


}
