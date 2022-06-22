package com.jd.bluedragon.distribution.jy.service.send;


import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.send.JySendTransferLogEntity;
import java.util.Map;

/**
 * 发货任务迁移记录表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public interface JySendTransferLogService {

  int saveTransferLog(VehicleSendRelationDto vehicleSendRelationDto);

}

