package com.jd.bluedragon.distribution.open.entity;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: BatchPagerRequest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 12:33
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class BatchPageRequest {

    /**
     * 调用信息
     */
    private RequestProfile requestProfile;

    public RequestProfile getRequestProfile() {
        return requestProfile;
    }

    public void setRequestProfile(RequestProfile requestProfile) {
        this.requestProfile = requestProfile;
    }
}
