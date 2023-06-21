package com.yc.reid.utils.cache;

import android.content.Context;

/**
 * 作者：yc on 2018/6/26.
 * 邮箱：501807647@qq.com
 * 版本：v1.0
 */

public class ShareIsLoginCache {

    private static Context act;

    private static class Holder {
        private static final ShareIsLoginCache INSTANCE = new ShareIsLoginCache();
    }

    private ShareIsLoginCache() {
    }

    public static final ShareIsLoginCache getInstance(Context act) {
        ShareIsLoginCache.act = act;
        return ShareIsLoginCache.Holder.INSTANCE;
    }

    private String login = "LOGIN";

    public void save(boolean isLogin){
        ACache.get(act).put(login, isLogin, 2592000);
    }

    public boolean getIsLogin(){
        Object asObject = ACache.get(act).getAsObject(login);
        if (asObject != null){
            return true;
        }
        return false;
    }

    public void remove(){
        ACache.get(act).remove(login);
    }

}
