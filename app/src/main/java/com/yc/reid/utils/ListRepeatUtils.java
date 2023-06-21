package com.yc.reid.utils;

import android.os.Build;

import com.blankj.utilcode.util.LogUtils;
import com.yc.reid.bean.DataBean;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author nike
 * @Date 2023/6/6 15:07
 * @Description
 */
public class ListRepeatUtils {

    /**
     * 使用 Stream 去重
     * @param list
     */
    public static List<DataBean> repeat(List<DataBean> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return list.stream().distinct().collect(Collectors.toList());
        }else {
            return null;
        }
    }

    /**
     * 判断list是否存在某个字段
     * @param list
     * @param fieldName
     * @return
     */
    public static String isListExists(List<DataBean> list, String fieldName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String s = list.stream().findAny().map(DataBean::getLabelTag).orElse(fieldName);
            if (s.equals(fieldName)){
                LogUtils.e(s, fieldName);
                return s;
            }
//            return list.stream().anyMatch(obj -> obj.getAssetNo().equals(fieldName));
        }
        LogUtils.e("你也走了吗", fieldName);
        return null;
    }

}
