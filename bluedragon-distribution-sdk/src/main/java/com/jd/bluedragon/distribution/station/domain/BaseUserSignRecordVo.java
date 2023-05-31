package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/31 11:11
 * @Description:基础用户签到信息
 */
public class BaseUserSignRecordVo implements Serializable {

    /**
     * 场地编码
     */
    private Integer siteCode;

    /**
     * 员工ERP|拼音|身份证号
     */
    private String userCode;
    /**
     * 签到人员名称
     */
    private String userName;


    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
