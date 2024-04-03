package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.basedata.response.StreamlinedBasicSite;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingSendBatchCodeDetailDto;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingSendBatchCodeDetailRes;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.SendFlowDto;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntityQueryDto;
import com.jd.bluedragon.distribution.jy.constants.JyPickingSendTaskEnum;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingSendDestinationDetailDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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

    @Autowired
    private DmsConfigManager dmsConfigManager;

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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyPickingSendDestinationServiceImpl.findOrGenerateBatchCode",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public String findOrGenerateBatchCode(Long curSiteId, Long nextSiteId, User user, Integer taskType) {
        String batchCode = jyPickingSendDestinationDetailDao.fetchLatestNoCompleteBatchCode(curSiteId, nextSiteId, taskType);
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
            insertEntity.setTaskType(taskType);
            jyPickingSendDestinationDetailDao.insertSelective(insertEntity);
        }
        return batchCode;
    }
    @Override
    public boolean existSendNextSite(Long curSiteId, Long nextSiteId, Integer taskType) {
        JyGroupSortCrossDetailEntity queryEntity = new JyGroupSortCrossDetailEntity();
        queryEntity.setTemplateCode(this.getPickingSendTemplateCode(curSiteId.intValue(), taskType));
        queryEntity.setStartSiteId(curSiteId);
        queryEntity.setEndSiteId(nextSiteId);
        JyGroupSortCrossDetailEntity entity = jyGroupSortCrossDetailService.selectOneByFlowAndTemplateCode(queryEntity);
        if(!Objects.isNull(entity)) {
            return true;
        }
        return false;
    }

    private String getPickingSendTemplateCode(Integer siteId, Integer taskType) {
        return PickingGoodTaskTypeEnum.AVIATION.getCode().equals(taskType)
                ? Constants.AVIATION_TEMPLATE_PREFIX + siteId : Constants.RAIL_TEMPLATE_PREFIX + siteId;
    }

    @Override
    public Boolean finishSendTask(FinishSendTaskReq req) {
        Date now = new Date();
        JyPickingSendDestinationDetailEntity entity = new JyPickingSendDestinationDetailEntity();
        entity.setStatus(JyPickingSendTaskEnum.TO_SEAL.getCode());
        entity.setSendCode(req.getSendCode());
        entity.setCompleteTime(now);
        entity.setUpdateTime(now);
        entity.setUpdateUserErp(req.getUser().getUserErp());
        entity.setUpdateUserName(req.getUser().getUserName());
        entity.setTaskType(req.getTaskType());
        entity.setScanItemNum(req.getScanItemNum());
        jyPickingSendDestinationDetailDao.finishSendTask(entity);
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
     *
     * @param req
     * @return 添加成功返回true，否则返回false
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyPickingSendDestinationServiceImpl.addSendFlow",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Boolean> addSendFlow(SendFlowAddReq req) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        Integer currentSiteId = req.getCurrentOperate().getSiteCode();
        String cacheKey = String.format(CacheKeyConstants.CACHE_KEY_AIR_RAIL_ADD_SEND_FLOW, req.getCurrentOperate().getSiteCode());
        if (cacheService.exists(cacheKey)) {
            result.customMessage(InvokeResult.AIR_RAIL_SEND_FLOW_ADD_FAIL_CODE, InvokeResult.AIR_RAIL_SEND_FLOW_ADD_FAIL_MESSAGE);
            return result;
        }
        try {
            cacheService.setNx(cacheKey, currentSiteId, 5, TimeUnit.SECONDS);
            String templateCode = getPickingSendTemplateCode(currentSiteId, req.getTaskType());
            List<JyGroupSortCrossDetailEntity> entities = new ArrayList<>();
            List<StreamlinedBasicSite> siteList = filterExistedSendFlow(req, result);
            if (!result.codeSuccess()) {
                return result;
            }
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
                    logWarn("JyPickingSendDestinationServiceImpl.addSendFlow 添加流向失败 {}", JsonHelper.toJson(req));
                    result.error("流向添加失败！请重试！");
                    return result;
                }
            }
        } catch (Exception e) {
            logWarn("JyPickingSendDestinationServiceImpl.addSendFlow 添加流向异常 {}", JsonHelper.toJson(req), e);
            result.error("流向添加异常！请联系分拣小秘处理！");
            return result;
        } finally {
            cacheService.del(cacheKey);
        }
        return result;
    }

    /**
     * 过滤已存在的流向
     * @param req
     * @return nextSiteList 过滤后的站点列表
     */
    private List<StreamlinedBasicSite> filterExistedSendFlow(SendFlowAddReq req, InvokeResult<Boolean> invokeResult) {

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
        int total = flowList.size() + nextSiteList.size();
        if (total > dmsConfigManager.getUccPropertyConfiguration().getSendFlowLimit()) {
            invokeResult.customMessage(InvokeResult.AIR_RAIL_SEND_FLOW_EXCEED_LIMIT_CODE, InvokeResult.AIR_RAIL_SEND_FLOW_EXCEED_LIMIT_MESSAGE);
            return new ArrayList<>();
        }
        return nextSiteList;
    }

    /**
     * 获取发送流程查询DTO
     * @param req 基础请求对象
     * @return 返回JyGroupSortCrossDetailEntityQueryDto对象
     */
    private JyGroupSortCrossDetailEntityQueryDto getSendFlowQueryDto(SendFlowReq req) {
        JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();

        int startSiteId = req.getCurrentOperate().getSiteCode();
        String templateCode = getPickingSendTemplateCode(startSiteId, req.getTaskType());
        queryDto.setStartSiteId((long) startSiteId);
        queryDto.setTemplateCode(templateCode);
        queryDto.setGroupCode(Constants.EMPTY_FILL);

        return queryDto;
    }

    /**
     * 获取发送流程查询DTO
     * @param req 基础请求对象
     * @return 返回JyGroupSortCrossDetailEntityQueryDto对象
     */
    private JyGroupSortCrossDetailEntityQueryDto getSendFlowQueryDto(SendFlowAddReq req) {
        JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();

        int startSiteId = req.getCurrentOperate().getSiteCode();
        String templateCode = getPickingSendTemplateCode(startSiteId, req.getTaskType());
        queryDto.setStartSiteId((long) startSiteId);
        queryDto.setTemplateCode(templateCode);

        return queryDto;
    }

    @Override
    public boolean deleteSendFlow(SendFlowDeleteReq req) {
        Integer currentSiteId = req.getCurrentOperate().getSiteCode();
        String templateCode = getPickingSendTemplateCode(currentSiteId, req.getTaskType());
        JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
        entity.setTemplateCode(templateCode);
        entity.setGroupCode(req.getGroupCode());
        entity.setStartSiteId((long) currentSiteId);
        entity.setEndSiteId((long) req.getNextSiteId());
        entity.setUpdateTime(new Date());
        entity.setUpdateUserErp(req.getUser().getUserErp());
        entity.setUpdateUserName(req.getUser().getUserName());
        return jyGroupSortCrossDetailService.deleteSendFlow(entity);
    }


    @Override
    public JyPickingSendDestinationDetailEntity getSendDetailBySiteId(Long createSiteId, Long nextSiteId, Integer taskType) {
        JyPickingSendDestinationDetailEntity entity = new JyPickingSendDestinationDetailEntity();
        entity.setCreateSiteId(createSiteId);
        entity.setNextSiteId(nextSiteId);
        entity.setTaskType(taskType);
        entity.setStatus(JyPickingSendTaskEnum.SENDING.getCode());
        return jyPickingSendDestinationDetailDao.getSendDetailBySiteId(entity);

    }

    @Override
    public String fetchLatestNoCompleteBatchCode(Long curSiteId, Long nextSiteId, Integer taskType) {
        return jyPickingSendDestinationDetailDao.fetchLatestNoCompleteBatchCode(curSiteId, nextSiteId, taskType);
    }

    @Override
    public InvokeResult<PickingSendBatchCodeDetailRes> pageFetchSendBatchCodeDetailList(PickingSendBatchCodeDetailReq req) {
        InvokeResult<PickingSendBatchCodeDetailRes> res = new InvokeResult<>();
        PickingSendBatchCodeDetailRes resData = new PickingSendBatchCodeDetailRes();
        res.setData(resData);
        //select by condition
        JyPickingSendDestinationDetailCondition condition = new JyPickingSendDestinationDetailCondition();
        condition.setCreateSiteId((long)req.getCurrentOperate().getSiteCode());
        condition.setNextSiteId(req.getNextSiteId().longValue());
        condition.setStatus(req.getBatchCodeStatus());
        condition.setTaskType(req.getTaskType());
        condition.setPageSize(req.getPageSize());
        condition.setOffset(this.getOffset(req.getPageNum(), req.getPageSize()));
        List<JyPickingSendDestinationDetailEntity> entityList = jyPickingSendDestinationDetailDao.pageFetchSendBatchCodeDetailList(condition);
        if(CollectionUtils.isEmpty(entityList)) {
            res.setMessage("查询为空");
            return res;
        }
        //convert resultSet to dto
        List<PickingSendBatchCodeDetailDto> pickingSendBatchCodeDetailDtoList = new ArrayList<>();
        entityList.forEach(entity -> {
            PickingSendBatchCodeDetailDto dto = new PickingSendBatchCodeDetailDto();
            dto.setSendCode(entity.getSendCode());
            dto.setFirstScanTime(entity.getFirstScanTime());
            dto.setCompleteTime(entity.getCompleteTime());
            dto.setScanItemNum(entity.getScanItemNum());
            pickingSendBatchCodeDetailDtoList.add(dto);
        });
        resData.setPickingSendBatchCodeDetailDtoList(pickingSendBatchCodeDetailDtoList);
        return res;
    }
    //分页偏移量
    private Integer getOffset(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return 0;
        }else {
            return  (pageNum - 1) * pageSize;
        }
    }


    @Override
    public void delBatchCodes(DelBatchCodesReq req) {
        JyPickingSendDestinationDetailCondition condition = new JyPickingSendDestinationDetailCondition();
        condition.setSendCodeList(req.getSendCodeList());
        condition.setCreateSiteId((long)req.getCurrentOperate().getSiteCode());
        condition.setNextSiteId(req.getNextSiteId().longValue());
        condition.setTaskType(req.getTaskType());

        condition.setUpdateTime(new Date());
        condition.setUpdateUserErp(req.getUser().getUserErp());
        condition.setUpdateUserName(req.getUser().getUserName());
        jyPickingSendDestinationDetailDao.delBatchCodes(condition);
    }
}
