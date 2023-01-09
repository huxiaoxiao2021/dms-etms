package com.jd.bluedragon.distribution.open;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.open.entity.*;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open
 * @ClassName: JYCenterService
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 10:00
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface JYCenterService {

    /**
     * 批次号的创建接口
     * @param jySendCodeRequest 创建接口请求对象
     * @return 返回批次号
     */
    InvokeResult<String> createSendCode(JYSendCodeRequest jySendCodeRequest);

    /**
     * 批量验货接口
     * @param batchInspectionPageRequest 批量验货请求参数
     * @return 返回是否成功
     */
    InvokeResult<Boolean> batchInspectionWithPage(BatchInspectionPageRequest batchInspectionPageRequest);

    /**
     * 批量分拣接口
     * @param batchSortingPageRequest 批量分拣请求参数
     * @return 返回是否成功
     */
    InvokeResult<Boolean> batchSortingWithPage(BatchSortingPageRequest batchSortingPageRequest);

    /**
     * 批量发货接口
     * @param batchSendPageRequest 批量发货请求参数
     * @return 返回是否成功
     */
    InvokeResult<Boolean> batchSendWithPage(BatchSendPageRequest batchSendPageRequest);

    /**
     * 批量称重量方接口
     * @param batchWeightVolumeRequest 批量称重量方请求参数
     * @return 返回是否成功
     */
    InvokeResult<Boolean> batchWeightVolume(BatchWeightVolumeRequest batchWeightVolumeRequest);
}
