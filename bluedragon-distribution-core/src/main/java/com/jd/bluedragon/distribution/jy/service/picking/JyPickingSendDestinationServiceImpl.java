package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.basedata.response.StreamlinedBasicSite;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.FinishSendTaskReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.SendFlowAddReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.SendFlowDeleteReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.SendFlowReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.SendFlowDto;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntityQueryDto;
import com.jd.bluedragon.distribution.jy.constants.JyPickingSendTaskEnum;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingSendDestinationDetailDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
import org.apache.commons.collections4.CollectionUtils;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum.AVIATION_RAILWAY_PICKING_GOOD_POSITION;

/**
 * 空铁提货发货服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:20
 * @Description
 */
@Service
public class JyPickingSendDestinationServiceImpl implements JyPickingSendDestinationService{

    private static final Logger log = LoggerFactory.getLogger(JyPickingSendDestinationServiceImpl.class);
    private static final String TEMPLATE_NAME = "空铁提货岗流向模板";

    @Autowired
    private JyGroupSortCrossDetailService jyGroupSortCrossDetailService;
    @Autowired
    private JyPickingSendDestinationDetailDao jyPickingSendDestinationDetailDao;
    @Autowired
    private IJySendVehicleService jySendVehicleService;
    @Autowired
    private CacheService cacheService;

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

    @Override
    public Boolean finishSendTask(FinishSendTaskReq req) {
        JyPickingSendDestinationDetailEntity entity = new JyPickingSendDestinationDetailEntity();
        entity.setStatus(JyPickingSendTaskEnum.TO_SEND.getCode());
        entity.setSendCode(req.getSendCode());
        entity.setCreateSiteId((long) req.getCurrentOperate().getSiteCode());
        entity.setNextSiteId((long) req.getNextSiteId());
        entity.setCompleteTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setUpdateUserErp(req.getUser().getUserErp());
        entity.setUpdateUserName(req.getUser().getUserName());
        jyPickingSendDestinationDetailDao.updateSendTaskStatus(entity);
        return true;
    }

    /**
     * 获取流向列表
     * @param req
     * @return
     */
    @Override
    public List<SendFlowDto> listSendFlowInfo(SendFlowReq req) {
        JyGroupSortCrossDetailEntityQueryDto queryDto = getSendFlowQueryDto(req);
        List<JyGroupSortCrossDetailEntity> flowList = jyGroupSortCrossDetailService.selectByCondition(queryDto);
        List<SendFlowDto> dtoList = new ArrayList<>();
        for (JyGroupSortCrossDetailEntity entity : flowList) {
            SendFlowDto dto = new SendFlowDto();
            dto.setNextSiteId(entity.getEndSiteId().intValue());
            dto.setNextSiteName(entity.getEndSiteName());
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 添加流向
     * @param req
     * @return 添加成功返回true，否则返回false
     */
    @Override
    public boolean addSendFlow(SendFlowAddReq req) {
        String cacheKey = String.format(CacheKeyConstants.CACHE_KEY_AIR_RAIL_ADD_SEND_FLOW, req.getCurrentOperate().getSiteCode());
        if (cacheService.exists(cacheKey)) {
            return false;
        }
        try {
            cacheService.setEx(cacheKey, req.getCurrentOperate().getSiteCode(), 5, TimeUnit.SECONDS);
            String templateCode = Constants.AVIATION_RAIL_TEMPLATE_PREFIX + req.getCurrentOperate().getSiteCode();
            List<JyGroupSortCrossDetailEntity> entities = new ArrayList<>();
            List<StreamlinedBasicSite> siteList = filterExistedSendFlow(req);
            for (StreamlinedBasicSite basicSite : siteList) {
                JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
                entity.setGroupCode(req.getGroupCode());
                entity.setTemplateCode(templateCode);
                entity.setTemplateName(TEMPLATE_NAME);
                entity.setCreateTime(new Date());
                entity.setCreateUserErp(req.getUser().getUserErp());
                entity.setCreateUserName(req.getUser().getUserName());
                entity.setCrossCode(Constants.EMPTY_FILL);
                entity.setEndSiteId(basicSite.getSiteCode().longValue());
                entity.setEndSiteName(basicSite.getSiteName());
                entity.setStartSiteId((long) req.getCurrentOperate().getSiteCode());
                entity.setStartSiteName(req.getCurrentOperate().getSiteName());
                entity.setTabletrolleyCode(Constants.EMPTY_FILL);
                entity.setFuncType(AVIATION_RAILWAY_PICKING_GOOD_POSITION.getCode());

                entities.add(entity);
            }
            if (CollectionUtils.isNotEmpty(entities)) {
                boolean success = jyGroupSortCrossDetailService.batchAddGroup(entities);
                if (!success) {
                    return false;
                }
            }
        } catch (Exception e) {
            logWarn("JyPickingSendDestinationServiceImpl.addSendFlow 添加流向异常 {}", JsonHelper.toJson(req));
            return false;
        } finally {
            cacheService.del(cacheKey);
        }
        return true;
    }

    /**
     * 过滤已存在的流向
     * @param req
     * @return nextSiteList 过滤后的站点列表
     */
    private List<StreamlinedBasicSite> filterExistedSendFlow(SendFlowAddReq req) {

        JyGroupSortCrossDetailEntityQueryDto queryDto = getSendFlowQueryDto(req);
        List<JyGroupSortCrossDetailEntity> flowList = jyGroupSortCrossDetailService.selectByCondition(queryDto);
        Set<Integer> siteIdSet = flowList.stream().map(site -> site.getEndSiteId().intValue()).collect(Collectors.toSet());
        List<StreamlinedBasicSite> nextSiteList = new ArrayList<>();
        for (StreamlinedBasicSite nextSite : req.getSiteList()) {
            if (!siteIdSet.contains(nextSite.getSiteCode())) {
                nextSiteList.add(nextSite);
                siteIdSet.add(nextSite.getSiteCode());
            }
        }
        return nextSiteList;
    }

    /**
     * 获取发送流程查询DTO
     * @param req 基础请求对象
     * @return 返回JyGroupSortCrossDetailEntityQueryDto对象
     */
    private JyGroupSortCrossDetailEntityQueryDto getSendFlowQueryDto(BaseReq req) {
        JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();

        int startSiteId = req.getCurrentOperate().getSiteCode();
        String templateCode = Constants.AVIATION_RAIL_TEMPLATE_PREFIX + startSiteId;
        String groupCode = req.getGroupCode();
        queryDto.setStartSiteId((long) startSiteId);
        queryDto.setTemplateCode(templateCode);
        queryDto.setGroupCode(groupCode);

        return queryDto;
    }

    @Override
    public boolean deleteSendFlow(SendFlowDeleteReq req) {
        String templateCode = Constants.AVIATION_RAIL_TEMPLATE_PREFIX + req.getCurrentOperate().getSiteCode();
        JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
        entity.setTemplateCode(templateCode);
        entity.setGroupCode(req.getGroupCode());
        entity.setStartSiteId((long) req.getCurrentOperate().getSiteCode());
        entity.setEndSiteId((long) req.getNextSiteId());
        entity.setUpdateTime(new Date());
        entity.setUpdateUserErp(req.getUser().getUserErp());
        entity.setUpdateUserName(req.getUser().getUserName());
        return jyGroupSortCrossDetailService.deleteSendFlow(entity);
    }
}
