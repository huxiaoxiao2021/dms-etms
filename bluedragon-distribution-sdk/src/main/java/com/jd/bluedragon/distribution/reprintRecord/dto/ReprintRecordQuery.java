package com.jd.bluedragon.distribution.reprintRecord.dto;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;

/**
 * 包裹补打查询参数
 *
 * @author fanggang7
 * @time 2020-11-01 22:04:27 周日
 */
public class ReprintRecordQuery extends BasePagerCondition implements Serializable {

    private static final long serialVersionUID = 3306728153817488358L;

    /*
     * 条码号
     */
    private String barCode;

    /*
     * 站点编号
     */
    private Integer siteCode;

    /**
     * 操作人erp或者姓名
     */
    private String operatorErpOrName;

    private String operateTimeFromStr;

    private String operateTimeToStr;

    private Date operateTimeFrom;

    private Date operateTimeTo;

    private Integer isDelete;

    private Integer pageSize;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getOperatorErpOrName() {
        return operatorErpOrName;
    }

    public void setOperatorErpOrName(String operatorErpOrName) {
        this.operatorErpOrName = operatorErpOrName;
    }

    public String getOperateTimeFromStr() {
        return operateTimeFromStr;
    }

    public void setOperateTimeFromStr(String operateTimeFromStr) {
        this.operateTimeFromStr = operateTimeFromStr;
    }

    public String getOperateTimeToStr() {
        return operateTimeToStr;
    }

    public void setOperateTimeToStr(String operateTimeToStr) {
        this.operateTimeToStr = operateTimeToStr;
    }

    public Date getOperateTimeFrom() {
        return operateTimeFrom;
    }

    public void setOperateTimeFrom(Date operateTimeFrom) {
        this.operateTimeFrom = operateTimeFrom;
    }

    public Date getOperateTimeTo() {
        return operateTimeTo;
    }

    public void setOperateTimeTo(Date operateTimeTo) {
        this.operateTimeTo = operateTimeTo;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.setLimit(pageSize);
    }
}
