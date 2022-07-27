package com.jd.bluedragon.common.dto.identity;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.common.dto.identity
 * @ClassName: IdentityRecogniseRequest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/7/25 18:56
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class IdentityRecogniseRequest {

    private String picUrl;

    private Integer siteCode;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }
}
