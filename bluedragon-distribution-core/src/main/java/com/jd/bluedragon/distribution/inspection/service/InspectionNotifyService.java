package com.jd.bluedragon.distribution.inspection.service;

import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;

/**
 * Created by wangtingwei on 2016/2/22.
 */
public interface InspectionNotifyService {
    void send(InspectionMQBody body);

    /**
     * 按运单维度发送验货消息，作用与 {@link InspectionNotifyService#send(InspectionMQBody)} 方法相同
     * @param request
     */
    void sendMQFromRequest(InspectionRequest request);
}
