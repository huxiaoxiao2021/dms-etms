package com.jd.bluedragon.distribution.open.entity;

import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: BatchWeightVolumeRequest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 15:52
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class BatchWeightVolumeRequest {

    /**
     * 接口调用信息
     */
    private RequestProfile requestProfile;

    /**
     * 称重量方明细
     */
    private List<WeightVolumeOperateInfo> detailList;

    public RequestProfile getRequestProfile() {
        return requestProfile;
    }

    public void setRequestProfile(RequestProfile requestProfile) {
        this.requestProfile = requestProfile;
    }

    public List<WeightVolumeOperateInfo> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<WeightVolumeOperateInfo> detailList) {
        this.detailList = detailList;
    }
}
