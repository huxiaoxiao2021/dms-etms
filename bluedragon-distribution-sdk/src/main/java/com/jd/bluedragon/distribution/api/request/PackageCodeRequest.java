package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description 原包分拣发货
 * @author jinjingcheng
 * @date 2018/6/22.
 */
public class PackageCodeRequest implements Serializable{

    private static final long serialVersionUID = -2943429031160869672L;
    /**当前分拣中心id*/
    private Integer distributeId;
    /**分拣中心名称 */
    private String distributeName;
    /**操作人id */
    private Integer operatorId;
    /**操作人姓名 */
    private String operatorName;
    /**操作时间 */
    private String operateTime;
    /**批次号 */
    private String sendCode;
    /** 下一个分拣中心id*/
    private Integer receiveSiteCode;
    /**	包裹号明细 */
    private List<String> packageList;

    public Integer getDistributeId() {
        return distributeId;
    }

    public void setDistributeId(Integer distributeId) {
        this.distributeId = distributeId;
    }

    public String getDistributeName() {
        return distributeName;
    }

    public void setDistributeName(String distributeName) {
        this.distributeName = distributeName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public List<String> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<String> packageList) {
        this.packageList = packageList;
    }
}
