package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendTransferLogDao;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.enums.TransferLogTypeEnum;
import com.jd.bluedragon.distribution.jy.send.JySendTransferLogEntity;
import com.jd.bluedragon.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("jySendTransferLogService")
public class JySendTransferLogServiceImpl implements JySendTransferLogService {

    @Autowired
    JySendTransferLogDao jySendTransferLogDao;

    @Override
    public int saveTransferLog(VehicleSendRelationDto dto) {
        if (ObjectHelper.isNotNull(dto.getSendCodes()) && dto.getSendCodes().size() > 0) {
            List<JySendTransferLogEntity> list = new ArrayList<>();
            if (ObjectHelper.isTrue(dto.getSameWayFlag())) {
                generateSameWayTransferLogEntity(dto, list);
            } else {
                generateNoSameWayTransferLogEntity(dto, list);
            }
            return jySendTransferLogDao.batchInsert(list);
        }
        return 0;
    }

    private void generateNoSameWayTransferLogEntity(VehicleSendRelationDto dto, List list) {
        Date now = new Date();
        for (String sendCode : dto.getSendCodes()) {
            JySendTransferLogEntity oldEntity = new JySendTransferLogEntity();
            oldEntity.setFromSendVehicleBizId(dto.getFromSendVehicleBizId());
            oldEntity.setFromSendVehicleDetailBizId(dto.getFromSendVehicleDetailBizId());
            oldEntity.setToSendVehicleBizId(dto.getToSendVehicleBizId());
            oldEntity.setToSendVehicleDetailBizId(dto.getToSendVehicleDetailBizId());
            oldEntity.setSendCode(sendCode);
            oldEntity.setType(TransferLogTypeEnum.NOT_SAME_WAY_TRANSFER_OLD_BATCH.getCode());
            oldEntity.setCreateTime(now);
            oldEntity.setUpdateTime(now);
            oldEntity.setCreateUserErp(dto.getUpdateUserErp());
            oldEntity.setCreateUserName(dto.getUpdateUserName());
            list.add(oldEntity);
        }
        JySendTransferLogEntity newEntity = new JySendTransferLogEntity();
        newEntity.setFromSendVehicleBizId(dto.getFromSendVehicleBizId());
        newEntity.setFromSendVehicleDetailBizId(dto.getFromSendVehicleDetailBizId());
        newEntity.setToSendVehicleBizId(dto.getToSendVehicleBizId());
        newEntity.setToSendVehicleDetailBizId(dto.getToSendVehicleDetailBizId());
        newEntity.setSendCode(dto.getNewSendCode());
        newEntity.setType(TransferLogTypeEnum.NOT_SAME_WAY_TRANSFER_NEW_BATCH.getCode());
        newEntity.setCreateTime(now);
        newEntity.setUpdateTime(now);
        newEntity.setCreateUserErp(dto.getUpdateUserErp());
        newEntity.setCreateUserName(dto.getUpdateUserName());
        list.add(newEntity);
    }

    private void generateSameWayTransferLogEntity(VehicleSendRelationDto dto, List list) {
        Date now = new Date();
        for (String sendCode : dto.getSendCodes()) {
            JySendTransferLogEntity entity = new JySendTransferLogEntity();
            entity.setFromSendVehicleBizId(dto.getFromSendVehicleBizId());
            entity.setFromSendVehicleDetailBizId(dto.getFromSendVehicleDetailBizId());
            entity.setToSendVehicleBizId(dto.getToSendVehicleBizId());
            entity.setToSendVehicleDetailBizId(dto.getToSendVehicleDetailBizId());
            entity.setSendCode(sendCode);
            entity.setType(ObjectHelper.isTrue(dto.getBindFlag()) ? TransferLogTypeEnum.SAME_WAY_BIND.getCode() : TransferLogTypeEnum.SAME_WAY_TRANSFER.getCode());
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            entity.setCreateUserErp(dto.getUpdateUserErp());
            entity.setCreateUserName(dto.getUpdateUserName());
            list.add(entity);
        }
    }

}
