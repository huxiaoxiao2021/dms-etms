package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendAbnormalEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.jy.dao.send.JySendDao;
import com.jd.bluedragon.distribution.jy.dto.send.TransferDto;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private JySendAggsService sendAggService;

    @Override
    public JySendEntity findSendRecordExistAbnormal(JySendEntity entity) {
        return sendDao.findSendRecordExistAbnormal(entity);
    }

    @Override
    public boolean findSendRecordExistAbnormal(Long startSiteId, String sendVehicleBizId) {
        // 是否存在强发或拦截
        JySendEntity existAbnormal = findSendRecordExistAbnormal(new JySendEntity(startSiteId, sendVehicleBizId));
        if (existAbnormal != null) {
            return true;
        }
        // 如果不存在强发或拦截，并且是转运任务，再次判断是否有不齐
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(startSiteId.intValue());
        if (baseStaffSiteOrgDto != null) {
            if (baseStaffSiteOrgDto.getSubType() != null && baseStaffSiteOrgDto.getSubType().equals(Constants.B2B_SITE_TYPE)) {
                JySendAggsEntity sendAggs = sendAggService.findSendAggExistAbnormal(sendVehicleBizId);
                if (sendAggs != null) {
                    return true;
                }
            }
        }
        return false;
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
            entity.setReceiveSiteId((long)BusinessUtil.getReceiveSiteCodeFromSendCode(dto.getNewSendCode()));
        }
        for (String sendCode:dto.getSendCodes()){
            entity.setSendCode(sendCode);
            rs =rs+sendDao.updateTransferProperBySendCode(entity);
        }
        return rs;
    }
}
