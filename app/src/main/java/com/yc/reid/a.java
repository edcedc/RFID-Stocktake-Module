package com.yc.reid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.yc.reid.base.User;
import com.yc.reid.bean.DataBean;
import com.yc.reid.bean.sql.StockChildSql;
import com.yc.reid.bean.sql.UserDataSql;
import com.yc.reid.ui.act.LoginAct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @Author a
 * @Date 2023/5/30 16:29
 * @Description
 */
public class a {



    String sdasd;


    boolean isPwd;

    Activity act;

    private List list = new ArrayList<DataBean>();
    private String string;





    public void  a(){
        Flowable.create((FlowableOnSubscribe<JSONObject>) e -> {

        }, BackpressureStrategy.BUFFER);

        Observable.timer(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                    }
                }).subscribe();

        Flowable.create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {

                    }
                },BackpressureStrategy.BUFFER)
//        .subscribeOn(Schedulers.newThread())
//        .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    private Subscription subscription;
                    @Override
                    public void onSubscribe(Subscription s) {
                    }
                    @Override
                    public void onNext(Integer integer) {

                    }
                    @Override
                    public void onError(Throwable t) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });


        int progress = Math.abs(new Random().nextInt() % 100);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.optJSONObject(i);
        }

        if (Arrays.asList(list).contains("silly")) {
            // true
        }

        for (int i = 0; i<8000; i++){

        }
        int v = (int) ((new BigDecimal((float) 40 / 60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue())*100);
        Integer i = 3;
        Float f = Float.valueOf(i);
        ThreadUtils.executeByCpu(new ThreadUtils.Task<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                Object o = null;
                return o;
            }

            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });

        string = new String();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //do something
            }
        }, 1000);    //延时1s执行




        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //do something
            }
        },1000);//延时1s执行
        UserDataSql userDataSql = LitePal.find(UserDataSql.class, 0);
//        Drawable left= getResources().getDrawable(R.mipmap.icon_21);
//        left.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
//        sendText.setCompoundDrawables(left,null ,null,null);//设置图片left这里如果是右边就放到第二个参数里面依次对应

    }

}
