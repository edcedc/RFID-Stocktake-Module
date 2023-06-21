package com.yc.reid.base;

import org.json.JSONObject;

public class User {

    private static class LazyHolder {
        private static final User INSTANCE = new User();
    }
    private User() {
    }
    public static final User getInstance() {
        return User.LazyHolder.INSTANCE;
    }


    private boolean login = false;
    private JSONObject userObj;
    private String userId;
    private String sessionId;

    public void setUserObj(JSONObject userObj) {
        this.userObj = userObj;
    }

    public JSONObject getUserObj() {
        return userObj;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean isLogin() {
        return login;
    }

    public void setUserId(String userId) {
        if (userObj != null) {
            this.userId = userObj.optString("id");
        }
    }

    public String getUserId() {
        if (userObj != null) {
            userId = userObj.optString("id");
        }
        return userId;
    }

    public void setSessionId(String sessionId) {
        if (userObj != null) {
            this.sessionId = userObj.optString("sessionId");
        }
    }

    public String getSessionId() {
        if (userObj == null) {
            return "";
        }
        sessionId = userObj.optString("sessionId");
        return sessionId;
    }
}
