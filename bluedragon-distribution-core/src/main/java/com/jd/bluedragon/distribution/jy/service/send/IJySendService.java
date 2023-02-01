package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.send.JySendEntity;

/**
 * @ClassName IJySendService
 * @Description
 * @Author wyh
 * @Date 2022/6/4 17:25
 **/
public interface IJySendService {

    /**
     * 查询一条发货任务的异常记录（强发或拦截）
     * @param entity
     * @return
     */
    JySendEntity findSendRecordExistAbnormal(JySendEntity entity);

    /**
     * 首先查询一条发货任务的异常记录（强发或拦截）
     * 如果上面条件不满足，如果是转运任务，则再次判断是否存在不齐
     * @param startSiteId
     * @param sendVehicleBizId
     * @return
     */
    boolean findSendRecordExistAbnormal(Long startSiteId, String sendVehicleBizId);

    /**
     * 按barCode查询一条扫描记录
     * @param entity
     * @return
     */
    JySendEntity queryByCodeAndSite(JySendEntity entity);

    int save(JySendEntity sendEntity);

    JySendEntity findByBizId(JySendEntity entity);

    /**
     * 任务迁移时更新 任务bizid和新的sendCode
     * @return
     */
    int updateTransferProperBySendCode(VehicleSendRelationDto dto);
}
