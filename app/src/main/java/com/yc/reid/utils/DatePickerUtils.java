/*
package com.yc.yyc.utils;

import android.app.Activity;
import android.graphics.Color;
import cn.addapp.pickers.common.LineConfig;
import cn.addapp.pickers.entity.City;
import cn.addapp.pickers.entity.County;
import cn.addapp.pickers.entity.Province;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.picker.DatePicker;
import cn.addapp.pickers.picker.SinglePicker;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.yc.yyc.bean.DataBean;
import com.yc.yyc.weight.AddressPickTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


*/
/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2019/7/24
 * Time: 11:17
 *//*

public class DatePickerUtils {



    public static void getYearMonthDayPicker(Activity act, String title, final OnYearMonthDayListener listener){
        String[] times = TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd")).split("-");
        Integer year = Integer.valueOf(times[0]);
        Integer mon = Integer.valueOf(times[1]);
        Integer day = Integer.valueOf(times[2]);

        final DatePicker picker = new DatePicker(act);
        picker.setTitleText(title);
        picker.setSubmitTextColor(Color.parseColor("#FA8099"));
        picker.setCanLoop(true);
        picker.setWheelModeEnable(true);
        picker.setSelectedTextColor(Color.parseColor("#FA8099"));
        picker.setTopPadding(15);
        picker.setRangeStart(year - 70, 1, 1);
        picker.setRangeEnd(year, 12, 1);
        picker.setSelectedItem(year, mon, day);
        picker.setWeightEnable(true);
        picker.setLineColor(Color.BLACK);
        picker.setOnDatePickListener((DatePicker.OnYearMonthDayPickListener) (year1, month, day1) -> {
            if (listener != null){
                listener.onTime(year1, month, day1);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    public static void onYearMonthPicker(Activity act, String title, final OnYearMonthDayListener listener) {
        String[] times = TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd")).split("-");
        Integer year = Integer.valueOf(times[0]);
        Integer mon = Integer.valueOf(times[1]);
        Integer day = Integer.valueOf(times[2]);

        DatePicker picker = new DatePicker(act, DatePicker.YEAR_MONTH);
//        picker.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        picker.setTitleText(title);
        picker.setSubmitTextColor(Color.parseColor("#FA8099"));
        picker.setSelectedTextColor(Color.parseColor("#FA8099"));
//        picker.setWidth((int) (picker.getScreenWidthPixels() * 0.6));
        picker.setTopPadding(15);
        picker.setRangeStart(year - 20, mon, day);
        picker.setRangeEnd(year, mon, day);
        picker.setSelectedItem(year, mon);
        picker.setCanLinkage(true);
        picker.setWeightEnable(true);
        picker.setWheelModeEnable(true);
        picker.setOnDatePickListener((DatePicker.OnYearMonthPickListener) (year1, month) -> {
            if (listener != null){
                listener.onTime(year1, month, null);
            }
        });
        picker.show();
    }

    public interface OnYearMonthDayListener{
        void onTime(String year, String month, String day);
    }
    public interface onOptionPicker{
        void onWheeled(int index, String item);
    }

    public static void onOptionPicker(Activity act, List<DataBean> listBean, final onOptionPicker listener) {
        List<String> list = new ArrayList<>();
        for (DataBean bean : listBean){
//            list.add(bean.getLabel());
        }
        SinglePicker picker = new SinglePicker(act, list);
        picker.setCanLoop(false);//不禁用循环
        picker.setLineVisible(true);
        picker.setSubmitTextColor(Color.parseColor("#FA8099"));
        picker.setSelectedTextColor(Color.parseColor("#FA8099"));
        picker.setTextSize(18);
        picker.setSelectedIndex(8);
        picker.setWheelModeEnable(false);
        picker.setTitleText("选择申请类型");
        //启用权重 setWeightWidth 才起作用
        picker.setWeightEnable(true);
        picker.setWeightWidth(1);
        LineConfig config = new LineConfig();
        config.setColor(Color.parseColor("#FA8099"));//线颜色
        config.setAlpha(120);//线透明度
//        config.setRatio(1);//线比率
        picker.setLineConfig(config);
        picker.setItemWidth(200);
        picker.setSelectedTextColor(Color.parseColor("#FA8099"));//前四位值是透明度
        picker.setUnSelectedTextColor(Color.parseColor("#999999"));
        picker.setOnSingleWheelListener((index, item) -> {

        });
        picker.setOnItemPickListener((OnItemPickListener<String>) (index, item) -> {
            if (listener != null)listener.onWheeled(index, item);
        });
        picker.show();
    }

    public static void onAddressPicker(Activity act, onAddressPickerListener addressPickerListener) {
        AddressPickTask task = new AddressPickTask(act);
        task.setHideProvince(false);
        task.setHideCounty(false);

        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                ToastUtils.showShort("数据初始化失败");
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                if (county == null) {
//                    showToast(province.getAreaName() + city.getAreaName());
                } else {
                    String address = province.getAreaName() + city.getAreaName() + county.getAreaName();
                    if (addressPickerListener != null){
                        addressPickerListener.onAddress(address);
                    }
                    LogUtils.e(address);
                }
            }
        });
        task.execute("广东", "广州", "越秀");
    }

    private onAddressPickerListener addressPickerListener;
    public interface onAddressPickerListener{
        void onAddress(String add);
    }

}
*/
