package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingSendDestinationDetailDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * 空铁提货发货服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:20
 * @Description
 */
@Service
public class JyPickingSendDestinationServiceImpl implements JyPickingSendDestinationService{

    private static final Logger log = LoggerFactory.getLogger(JyPickingSendDestinationServiceImpl.class);

    @Autowired
    private JyGroupSortCrossDetailService jyGroupSortCrossDetailService;
    @Autowired
    private JyPickingSendDestinationDetailDao jyPickingSendDestinationDetailDao;
    @Autowired
    private IJySendVehicleService jySendVehicleService;

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }


    @Override
    public String findOrGenerateBatchCode(Long curSiteId, Long nextSiteId, User user) {
        String batchCode = jyPickingSendDestinationDetailDao.fetchLatestNoCompleteBatchCode(curSiteId, nextSiteId);
        if(StringUtils.isBlank(batchCode)) {
            batchCode = jySendVehicleService.generateSendCode(curSiteId, nextSiteId, user.getUserErp());
            if(StringUtils.isBlank(batchCode)) {
                log.error("空铁提货岗发货批次号生成为空，操作场地={}，流向场地={}，erp={}", curSiteId, nextSiteId, user.getUserErp());
                throw new JyBizException("批次号生成为空，请尝试重新操作");
            }
            JyPickingSendDestinationDetailEntity insertEntity = new JyPickingSendDestinationDetailEntity(curSiteId, nextSiteId);
            insertEntity.setSendCode(batchCode);
            insertEntity.setStatus(JyPickingSendDestinationDetailEntity.STATUS_SENDING);
            insertEntity.setSealFlag(Constants.NUMBER_ZERO);
            Date date = new Date();
            insertEntity.setFirstScanTime(date);
            insertEntity.setCreateTime(date);
            insertEntity.setUpdateTime(insertEntity.getCreateTime());
            insertEntity.setCreateUserErp(user.getUserErp());
            insertEntity.setCreateUserName(user.getUserName());
            jyPickingSendDestinationDetailDao.insertSelective(insertEntity);
        }
        return batchCode;
    }
    @Override
    public boolean existSendNextSite(Long curSiteId, Long nextSiteId) {
        JyGroupSortCrossDetailEntity queryEntity = new JyGroupSortCrossDetailEntity();
        queryEntity.setTemplateCode(this.getPickingSendTemplateCode(curSiteId.intValue()));
        queryEntity.setStartSiteId(curSiteId);
        queryEntity.setEndSiteId(nextSiteId);
        JyGroupSortCrossDetailEntity entity = jyGroupSortCrossDetailService.selectOneByFlowAndTemplateCode(queryEntity);
        if(!Objects.isNull(entity)) {
            return true;
        }
        return false;
    }

    private String getPickingSendTemplateCode(Integer siteId) {
        return Constants.AVIATION_RAIL_TEMPLATE_PREFIX + siteId;
    }


}
