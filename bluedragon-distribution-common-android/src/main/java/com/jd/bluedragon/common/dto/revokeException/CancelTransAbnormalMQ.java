package com.jd.bluedragon.common.dto.revokeException;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liwenji
 * @date 2023-01-11 9:19
 */
public class CancelTransAbnormalMQ implements Serializable {

    /**
     * 异常编号
     */
    private String transAbnormalCode;

    private Date createTime;

    // 1:内网erp,2:外网PIN,3:APP-PIN请求,4:大屏erp请求 8:3pl-PIN，9:TFC
    private Integer source;

    private String userCode;

    /**
     * 异常类型
     */
    private String abnormalTypeCode;

    /**
     * 单据编码
     */
    private String referBillCode;
    
    public String getTransAbnormalCode() {
        return transAbnormalCode;
    }

    public void setTransAbnormalCode(String transAbnormalCode) {
        this.transAbnormalCode = transAbnormalCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAbnormalTypeCode() {
        return abnormalTypeCode;
    }

    public void setAbnormalTypeCode(String abnormalTypeCode) {
        this.abnormalTypeCode = abnormalTypeCode;
    }

    public String getReferBillCode() {
        return referBillCode;
    }

    public void setReferBillCode(String referBillCode) {
        this.referBillCode = referBillCode;
    }
}
