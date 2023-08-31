package com.jd.bluedragon.distribution.transport.service;


import com.jd.bluedragon.distribution.transport.dto.StopoverQueryDto;
import com.jd.dms.java.utils.sdk.base.Result;
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
    /*
        判断是否是合流车
        Integer siteCode,
        String transWorkCode,
        String sealCarCode,
        String simpleCode,
        String vehicleNumber
     */
    String isMergeCar(Integer siteCode, String transWorkCode, String sealCarCode,
                                           String simpleCode, String vehicleNumber);

    /**
     * 查询经停装卸信息类型
     * @return 经停数据结果
     * @author fanggang7
     * @time 2023-08-18 11:10:49 周五
     */
    Result<Integer> queryStopoverLoadAndUnloadType(StopoverQueryDto stopoverQueryDto);
}
