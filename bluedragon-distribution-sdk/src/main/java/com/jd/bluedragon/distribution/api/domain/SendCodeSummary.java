package com.jd.bluedragon.distribution.api.domain;

import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;

import java.util.List;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/22
 */
public class SendCodeSummary {

    /**
     * 箱内包裹总数
     */
    private Integer packageInBoxNum;

    /**
     * 箱外包裹总数
     */
    private Integer packageOutBoxNum;

    /**
     * 包裹总数
     */
    private Integer packageNum;

    /**
     * 批次明细信息
     */
    private List<SendBoxDetailResponse> sendCodeDetail;

    public Integer getPackageInBoxNum() {
        return packageInBoxNum;
    }

    public void setPackageInBoxNum(Integer packageInBoxNum) {
        this.packageInBoxNum = packageInBoxNum;
    }

    public Integer getPackageOutBoxNum() {
        return packageOutBoxNum;
    }

    public void setPackageOutBoxNum(Integer packageOutBoxNum) {
        this.packageOutBoxNum = packageOutBoxNum;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public List<SendBoxDetailResponse> getSendCodeDetail() {
        return sendCodeDetail;
    }

    public void setSendCodeDetail(List<SendBoxDetailResponse> sendCodeDetail) {
        this.sendCodeDetail = sendCodeDetail;
    }
}
