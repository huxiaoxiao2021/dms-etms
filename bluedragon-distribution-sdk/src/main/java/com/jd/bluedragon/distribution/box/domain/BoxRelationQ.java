package com.jd.bluedragon.distribution.box.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName BoxRelationQ
 * @Description
 * @Author wyh
 * @Date 2020/12/23 17:55
 **/
public class BoxRelationQ extends BasePagerCondition implements Serializable {

    private static final long serialVersionUID = -3900107470786598212L;

    /**
     * 分拣中心
     */
    private Long siteCode;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 用户ERP
     */
    private String userErp;

    /**
     * 关联箱号
     */
    private String relationBoxCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 箱号集合
     */
    private List<String> boxCodes;

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getRelationBoxCode() {
        return relationBoxCode;
    }

    public void setRelationBoxCode(String relationBoxCode) {
        this.relationBoxCode = relationBoxCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public List<String> getBoxCodes() {
        return boxCodes;
    }

    public void setBoxCodes(List<String> boxCodes) {
        this.boxCodes = boxCodes;
    }
}
