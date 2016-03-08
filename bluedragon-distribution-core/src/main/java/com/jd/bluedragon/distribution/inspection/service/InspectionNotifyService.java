package com.jd.bluedragon.distribution.inspection.service;

import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;

/**
 * Created by wangtingwei on 2016/2/22.
 */
public interface InspectionNotifyService {
    void send(InspectionMQBody body) throws Throwable;
}
