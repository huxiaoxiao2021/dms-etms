package com.jd.bluedragon.distribution.financialForKA.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * 单号校验记录Dto
 *
 * @author: hujiping
 * @date: 2020/2/26 22:03
 */
public class WaybillCodeCheckDto extends DbEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     * */
    private String waybillCode;
    /**
     * 比较单号
     * */
    private String compareCode;
    /**
     * 操作站点
     * */
    private Integer operateSiteCode;
    /**
     * 操作站点名称
     * */
    private String operateSiteName;
    /**
     * 操作人ERP
     * */
    private String operateErp;
    /**
     * 商家名称
     * */
    private String busiName;
    /**
     * 商家编码
     * */
    private String busiCode;
    /**
     * 校验结果
     * */
    private Integer checkResult;
    /**
     * 操作时间
     * */
    private Date operateTime;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getCompareCode() {
        return compareCode;
    }

    public void setCompareCode(String compareCode) {
        this.compareCode = compareCode;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operaterErp) {
        this.operateErp = operaterErp;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public Integer getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(Integer checkResult) {
        this.checkResult = checkResult;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
