package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * Created by yanghongqiang on 2015/10/26.
 */
public class MonitorRequest extends JdRequest {
    private String postData;
    private String url;
    private String snk;
    private String token;
    private String passWord;

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnk() {
        return snk;
    }

    public void setSnk(String snk) {
        this.snk = snk;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
