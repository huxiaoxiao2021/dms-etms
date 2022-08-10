package com.jd.bluedragon.distribution.transport.service;


import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * 运输相关接口
 *
 * @author hujiping
 * @date 2022/7/12 11:13 AM
 */
public interface TransportRelatedService {

    /**
     * 校验运输任务
     *
     * @param siteCode
     * @param transWorkCode
     * @param sealCarCode
     * @param simpleCode
     * @param vehicleNumber
     * @return
     */
    ImmutablePair<Integer, String> checkTransportTask(Integer siteCode, String transWorkCode, String sealCarCode,
                                                      String simpleCode, String vehicleNumber);
}
