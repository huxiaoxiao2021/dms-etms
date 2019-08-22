package com.jd.bluedragon.common.dto.task.request;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/7/5
 */
public class TaskPdaRequest implements Serializable {
    private static final long serialVersionUID = -1L;

    /*
    任务类型
     */
    private Integer type;
    private String body;
    /*
    分拣中心ID
     */
    private Integer siteCode;
    /*
    关键词1
     */
    private String keyword1;
    /*
    关键词2
     */
    private String keyword2;
    /*
    接收单位ID
     */
    private Integer receiveSiteCode;
    /*
    箱号
     */
    private String boxCode;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(String keyword1) {
        this.keyword1 = keyword1;
    }

    public String getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(String keyword2) {
        this.keyword2 = keyword2;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
