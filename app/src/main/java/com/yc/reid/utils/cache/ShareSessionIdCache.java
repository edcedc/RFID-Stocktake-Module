package com.yc.reid.utils.cache;

import android.content.Context;

/**
 * 作者：yc on 2018/6/26.
 * 邮箱：501807647@qq.com
 * 版本：v1.0
 */

public class ShareSessionIdCache {

    private static Context act;

    private static class Holder {
        private static final ShareSessionIdCache INSTANCE = new ShareSessionIdCache();
    }

    private ShareSessionIdCache() {
    }

    public static final ShareSessionIdCache getInstance(Context act) {
        ShareSessionIdCache.act = act;
        return ShareSessionIdCache.Holder.INSTANCE;
    }

    private String SessionId = "SESSIONID";
    private String UserId = "USERID";

    public void saveUserId(String userId){
        ACache.get(act).put(UserId, userId, 2592000);
    }

    public String getUserId(){
        return ACache.get(act).getAsString(UserId);
    }

    public void save(String session){
        ACache.get(act).put(SessionId, session, 2592000);
    }

    public String getSessionId(){
        return ACache.get(act).getAsString(SessionId);
    }

    public void remove(){
        ACache.get(act).remove(SessionId);
        ACache.get(act).remove(UserId);
    }


}
