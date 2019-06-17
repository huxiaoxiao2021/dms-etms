package com.jd.bluedragon.distribution.external.gateway.dto.request;

import com.jd.bluedragon.distribution.api.request.PackageSendRequest;

/**
 * BatchSingleSendRequest
 * 批量一车一单发货
 * @author jiaowenqiang
 * @date 2019/6/11
 */
public class BatchSingleSendRequest {

    /**
     * 用户
     */
    private User user;

    /**
     * 当前站点
     */
    private CurrentOperate currentOperate;

    /**
     * 批次号
     */
    private String sendCode;
    /**
     * 箱号或包裹号
     */
    private String boxCode;
    /**
     * 分拣业务类型 '10' 正向 '20' 逆向 '30' 三方 '40' POP
     */
    private int businessType;
    /**
     * 目的地站点
     */
    private int receiveSiteCode;

    /**
     * 是否强制取消发货
     */
    private boolean isForceSend;


    public static PackageSendRequest convertToPackageSendRequest(BatchSingleSendRequest request){
        PackageSendRequest packageSendRequest=new PackageSendRequest();

        packageSendRequest.setUserCode(request.getUser().getUserCode());
        packageSendRequest.setUserName(request.getUser().getUserName());
        packageSendRequest.setSiteCode(request.currentOperate.getSiteCode());
        packageSendRequest.setSiteName(request.getCurrentOperate().getSiteName());

        packageSendRequest.setIsForceSend(request.isForceSend);
        packageSendRequest.setReceiveSiteCode(request.getReceiveSiteCode());
        packageSendRequest.setSendCode(request.getSendCode());
        packageSendRequest.setBoxCode(request.getBoxCode());
        packageSendRequest.setBusinessType(request.getBusinessType());

        return packageSendRequest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public int getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(int receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public boolean isForceSend() {
        return isForceSend;
    }

    public void setForceSend(boolean forceSend) {
        isForceSend = forceSend;
    }
}
