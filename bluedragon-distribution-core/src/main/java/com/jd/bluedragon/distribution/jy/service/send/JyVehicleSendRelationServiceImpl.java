package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendCodeDao;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCodeDto;
import com.jd.bluedragon.distribution.jy.dto.send.TransferDto;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class JyVehicleSendRelationServiceImpl implements JyVehicleSendRelationService{
    @Autowired
    JySendCodeDao jySendCodeDao;
    @Autowired
    NewSealVehicleService newSealVehicleService;

    @Override
    public List<String> querySendCodesByVehicleDetailBizId(String vehicleDetailBizId) {
        return jySendCodeDao.querySendCodesByVehicleDetailBizId(vehicleDetailBizId);
    }

    @Override
    public List<String> querySendCodesByVehicleBizId(String vehicleBizId) {
        return jySendCodeDao.querySendCodesByVehicleBizId(vehicleBizId);
    }

    @Override
    @Transactional
    public int updateVehicleSendRelation(VehicleSendRelationDto dto) {
        Integer rs =0;
        TransferDto transferDto = BeanUtils.copy(dto,TransferDto.class);
        for (String sendCode:dto.getSendCodes()){
            transferDto.setSendCode(sendCode);
            rs =rs+jySendCodeDao.updateBySendCode(transferDto);
        }
        return rs;
    }

    @Override
    public int deleteVehicleSendRelation(VehicleSendRelationDto dto) {
        Integer rs =0;
        TransferDto transferDto = BeanUtils.copy(dto,TransferDto.class);
        for (String sendCode:dto.getSendCodes()){
            transferDto.setSendCode(sendCode);
            rs =rs+jySendCodeDao.deleteVehicleSendRelation(transferDto);
        }
        return rs;
    }

    @Override
    public int deleteVehicleSendRelationByVehicleBizId(JySendCodeDto dto) {
        return jySendCodeDao.deleteVehicleSendRelationByVehicleBizId(dto);
    }

    @Override
    public int add(JySendCodeEntity jySendCodeEntity) {
        return jySendCodeDao.insert(jySendCodeEntity);
    }

    @Override
    public String findEarliestSendCode(String vehicleDetailBizId) {
        return jySendCodeDao.findEarliestSendCode(vehicleDetailBizId);
    }

    @Override
    public String findEarliestNoSealCarSendCode(String detailBiz) {
        List<String> sendCodes =querySendCodesByVehicleDetailBizId(detailBiz);
        for (String sendCode:sendCodes){
            if (!newSealVehicleService.checkSendCodeIsSealed(sendCode)){
                return sendCode;
            }
        }
        return null;
    }

}
