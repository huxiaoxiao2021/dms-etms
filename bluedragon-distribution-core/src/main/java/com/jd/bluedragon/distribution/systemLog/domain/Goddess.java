package com.jd.bluedragon.distribution.systemLog.domain;
import java.util.*;
/**
 *
 * Created by wangtingwei on 2017/2/16.
 */
public class Goddess {

    private static final long serialVersionUID=1L;

    public Goddess(){
        dateTime=new Date();
    }
    /**
     * 关键字
     */
    private String key;

    /**
     * 头部
     */
    private String head;

    /**
     * 正文
     */
    private String body;

    private Date dateTime;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
