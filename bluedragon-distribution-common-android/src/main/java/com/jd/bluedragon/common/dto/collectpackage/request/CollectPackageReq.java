package com.jd.bluedragon.common.dto.collectpackage.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
public class CollectPackageReq  extends BaseReq implements Serializable {
    private static final long serialVersionUID = 1524434357342779618L;

    private String barCode;
    private String boxCode;
    private String bizId;

    /**
     * 强制集包（运单路由信息- 不在允许集包的流向集合中时）
     */
    private boolean forceCollectPackage;

    /**
     * 弱拦截提示时，用户二次确认跳过拦截
     */
    private boolean skipInterceptChain;

    private boolean skipSealBoxCheck;

    public boolean getSkipSealBoxCheck(){
        return skipSealBoxCheck;
    }

    public void setSkipSealBoxCheck(boolean skipSealBoxCheck) {
        this.skipSealBoxCheck = skipSealBoxCheck;
    }

    private Integer taskStatus;

    public Integer getTaskStatus(){
        return taskStatus;
    }
    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public boolean getSkipInterceptChain() {
        return skipInterceptChain;
    }

    public void setSkipInterceptChain(boolean skipInterceptChain) {
        this.skipInterceptChain = skipInterceptChain;
    }

    public boolean getForceCollectPackage() {
        return forceCollectPackage;
    }

    public void setForceCollectPackage(boolean forceCollectPackage) {
        this.forceCollectPackage = forceCollectPackage;
    }

    /**
     * 集包目的地
     */
    private Long endSiteId;
    private String endSiteName;

    /**
     * 建包流向（箱号目的地）
     */
    private Long boxReceiveId;
    private String boxReceiveName;

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public void setBoxReceiveName(String boxReceiveName) {
        this.boxReceiveName = boxReceiveName;
    }

    public Long getBoxReceiveId(){
        return boxReceiveId;
    }

    public void setBoxReceiveId(Long boxReceiveId) {
        this.boxReceiveId = boxReceiveId;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public String getBoxReceiveName() {
        return boxReceiveName;
    }
}
