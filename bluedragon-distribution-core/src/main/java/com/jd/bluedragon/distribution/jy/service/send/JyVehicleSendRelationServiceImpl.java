package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendCodeDao;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCodeDto;
import com.jd.bluedragon.distribution.jy.dto.send.TransferDto;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.utils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveSealSendCode(List<JySendCodeEntity> jySendCodeEntityList) {
        JySendCodeEntity entity =new JySendCodeEntity();
        entity.setSendDetailBizId(jySendCodeEntityList.get(0).getSendDetailBizId());
        jySendCodeDao.deleteVehicleSendRelationByVehicleDetailBizId(entity);
        return jySendCodeDao.batchInsert(jySendCodeEntityList);
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

    @Override
    public List<JySendCodeEntity> querySendDetailBizIdBySendCode(List<String> sendCodes) {
        return jySendCodeDao.querySendDetailBizIdBySendCode(sendCodes);
    }

    /**
     * 根据任务ID查询批次数据
     *
     * @param vehicleBizId 任务业务ID
     * @return 批次数据列表
     * @author fanggang7
     * @time 2022-09-26 17:36:53 周一
     */
    @Override
    public List<JySendCodeEntity> queryByVehicleBizId(String vehicleBizId) {
        return jySendCodeDao.queryByVehicleBizId(vehicleBizId);
    }

    @Override
    public List<JySendCodeEntity> queryByVehicleDetailBizId(String vehicleDetailBizId) {
        return jySendCodeDao.queryByVehicleDetailBizId(vehicleDetailBizId);
    }

    @Override
    public List<String> findSendCodesByDetailBizIds(List<String> detailBizIdList) {
        if(CollectionUtils.isEmpty(detailBizIdList)) {
            return null;
        }
        return jySendCodeDao.findSendCodesByDetailBizIds(detailBizIdList);
    }
}
