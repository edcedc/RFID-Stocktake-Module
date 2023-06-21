package com.yc.reid.utils.cache;

import android.content.Context;

/**
 * 作者：yc on 2018/6/26.
 * 邮箱：501807647@qq.com
 * 版本：v1.0
 */

public class SharedAccount {

    private static Context act;

    private static class Holder {
        private static final SharedAccount INSTANCE = new SharedAccount();
    }

    private SharedAccount() {
    }

    public static final SharedAccount getInstance(Context act) {
        SharedAccount.act = act;
        return SharedAccount.Holder.INSTANCE;
    }

    private final String MOBILE_KEY = "mobile";
    private final String PASSWORD_KEY = "password";

    public void save(String mobile, String password) {
        ACache.get(act).put(MOBILE_KEY, mobile, 2592000);
        ACache.get(act).put(PASSWORD_KEY, password, 2592000);
    }

    public String getMobile(){
        return ACache.get(act).getAsString(MOBILE_KEY);
    }
    public String getPwd(){
        return ACache.get(act).getAsString(PASSWORD_KEY);
    }

    public void remove(){
//        ACache.get(act).remove(MOBILE_KEY);
        ACache.get(act).remove(PASSWORD_KEY);
    }

}
