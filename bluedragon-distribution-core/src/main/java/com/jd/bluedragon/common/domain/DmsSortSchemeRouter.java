package com.jd.bluedragon.common.domain;

import java.io.Serializable;

/**
 * Created by wuzuxiang on 2017/1/12.
 */
public class DmsSortSchemeRouter implements Serializable{
    private static final long serialVersionUID = 7666451437007377027L;

    /** 分拣计划类型：主表SortScheme 明细表SortSchemeDetail **/
    private String type;

    /** 消息内容 **/
    private String body;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "DmsSortSchemeRouter{" + "type='" + type + '\'' + ", body='" + body + '\'' + '}';
    }
}
