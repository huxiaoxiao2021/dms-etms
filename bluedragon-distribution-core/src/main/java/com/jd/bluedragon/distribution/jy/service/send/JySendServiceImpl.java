package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dao.send.JySendDao;
import com.jd.bluedragon.distribution.jy.dto.send.TransferDto;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.send.JySendEntity;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName JySendServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/6/4 17:26
 **/
@Service
public class JySendServiceImpl implements IJySendService{

    @Autowired
    private JySendDao sendDao;

    @Override
    public JySendEntity findSendRecordExistAbnormal(JySendEntity entity) {
        return sendDao.findSendRecordExistAbnormal(entity);
    }

    @Override
    public JySendEntity queryByCodeAndSite(JySendEntity entity) {
        return sendDao.queryByCodeAndSite(entity);
    }

    @Override
    public int save(JySendEntity sendEntity) {
        int rows = sendDao.updateByCondition(sendEntity);
        if (rows == Constants.NO_MATCH_DATA) {
            return sendDao.insert(sendEntity);
        }
        return rows;
    }

    @Override
    public JySendEntity findByBizId(JySendEntity entity) {
        return sendDao.findByBizId(entity);
    }

    @Override
    public int updateTransferProperBySendCode(VehicleSendRelationDto dto) {
        Integer rs =0;
        JySendEntity entity = new JySendEntity();
        entity.setSendVehicleBizId(dto.getToSendVehicleBizId());
        entity.setCreateSiteId(dto.getCreateSiteId());
        entity.setUpdateUserErp(dto.getUpdateUserErp());
        entity.setUpdateUserName(dto.getUpdateUserName());
        entity.setUpdateTime(new Date());
        if (ObjectHelper.isNotNull(dto.getNewSendCode())){
            entity.setNewSendCode(dto.getNewSendCode());
        }
        for (String sendCode:dto.getSendCodes()){
            entity.setSendCode(sendCode);
            rs =rs+sendDao.updateTransferProperBySendCode(entity);
        }
        return rs;
    }
}
