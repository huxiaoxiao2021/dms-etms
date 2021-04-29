package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 封车集齐监控报表未集齐查询参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-04-29 14:22:48 周日
 */
public class SealCarNotCollectedPo implements Serializable {

    private static final long serialVersionUID = 7731630185920878094L;

    /**
     * 分拣中心ID
     */
    private Long siteId;

    /**
     * 封车号
     */
    private String sealCarCode;

    /**
     * 封车号
     */
    private List<String> sealCarCodeList;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户id
     */
    private String userErp;

    /**
     * 用户姓名
     */
    private String userName;

    public Long getSiteId() {
        return siteId;
    }

    public SealCarNotCollectedPo setSiteId(Long siteId) {
        this.siteId = siteId;
        return this;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public SealCarNotCollectedPo setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
        return this;
    }

    public List<String> getSealCarCodeList() {
        return sealCarCodeList;
    }

    public SealCarNotCollectedPo setSealCarCodeList(List<String> sealCarCodeList) {
        this.sealCarCodeList = sealCarCodeList;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public SealCarNotCollectedPo setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserErp() {
        return userErp;
    }

    public SealCarNotCollectedPo setUserErp(String userErp) {
        this.userErp = userErp;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public SealCarNotCollectedPo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

}
