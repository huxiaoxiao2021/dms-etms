package com.jd.bluedragon.distribution.jy.service.comboard.impl;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.comboard.request.AddCTTReq;
import com.jd.bluedragon.common.dto.comboard.request.CTTGroupDataReq;
import com.jd.bluedragon.common.dto.comboard.request.CreateGroupCTTReq;
import com.jd.bluedragon.common.dto.comboard.request.RemoveCTTReq;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDataResp;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDto;
import com.jd.bluedragon.common.dto.comboard.response.CreateGroupCTTResp;
import com.jd.bluedragon.common.dto.comboard.response.TableTrolleyDto;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.constants.FocusEnum;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyGroupSortCrossDetailDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyCTTGroupUpdateReq;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.etms.waybill.domain.Waybill;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_PARAMETER_ERROR_CODE;
import static com.jd.bluedragon.distribution.jy.constants.JyPostEnum.SEND_SEAL_DMS;
import static com.jd.bluedragon.distribution.jy.constants.JyPostEnum.SEND_SEAL_WAREHOUSE;

/**
 * @author liwenji
 * @description TODO
 * @date 2022-11-17 18:22
 */
@Service
@Slf4j
public class JyGroupSortCrossDetailServiceImpl implements JyGroupSortCrossDetailService {

    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;

    @Autowired
    private JyGroupSortCrossDetailDao jyGroupSortCrossDetailDao;
    @Autowired
    private IGenerateObjectId genObjectId;
    
    public static final String COM_BOARD_SEND = "CTT%s";

    @Override
    public String createGroup(CreateGroupCTTReq request) {
        String templateCode = getTemplateCode();
        List<JyGroupSortCrossDetailEntity> jyGroupSortCrossDetailEntityList = 
                getJyGroupSortCrossDetailEntityList(templateCode, request.getTemplateName(), request, request.getTableTrolleyDtoList());
        if (this.createCTTGroup(jyGroupSortCrossDetailEntityList)) {
            return templateCode;
        }
        return null;
    }

    private boolean createCTTGroup(List<JyGroupSortCrossDetailEntity> jyGroupSortCrossDetailEntityList) {
        if (jyGroupSortCrossDetailDao.batchInsert(jyGroupSortCrossDetailEntityList) > 0) {
            return true;
        } else {
            log.error("创建混扫任务失败：{}", JsonHelper.toJson(jyGroupSortCrossDetailEntityList));
            return false;
        }
    }

    private List<JyGroupSortCrossDetailEntity> getJyGroupSortCrossDetailEntityList(String templateCode, String templateName, BaseReq request, List<TableTrolleyDto> tableTrolleyDtoList) {
        List<JyGroupSortCrossDetailEntity> list = new ArrayList<>();
        for (TableTrolleyDto tableTrolleyDto : tableTrolleyDtoList) {
            JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
            entity.setGroupCode(request.getGroupCode());
            entity.setTemplateCode(templateCode);
            entity.setTemplateName(templateName);
            entity.setCreateTime(new Date());
            entity.setCreateUserErp(request.getUser().getUserErp());
            entity.setCreateUserName(request.getUser().getUserName());
            entity.setCrossCode(tableTrolleyDto.getCrossCode());
            entity.setEndSiteId(tableTrolleyDto.getEndSiteId().longValue());
            entity.setEndSiteName(tableTrolleyDto.getEndSiteName());
            entity.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
            entity.setStartSiteName(request.getCurrentOperate().getSiteName());
            entity.setTabletrolleyCode(tableTrolleyDto.getTableTrolleyCode());
            entity.setFuncType(SEND_SEAL_DMS.getCode());
            list.add(entity);
        }
        return list;
    }

    private String getTemplateCode() {
        String key = String.format(COM_BOARD_SEND, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return key + StringHelper.padZero(redisJyBizIdSequenceGen.gen(key));
    }

    @Override
    public CTTGroupDataResp queryCTTGroupDataByGroupOrSiteCode(CTTGroupDataReq request) {
        CTTGroupDataResp resp = new CTTGroupDataResp();
        List<CTTGroupDto> cttGroupDtos;
        JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
        entity.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
        entity.setFuncType(SEND_SEAL_DMS.getCode());
        if (request.isGroupQueryFlag()) {
            entity.setGroupCode(request.getGroupCode());
            cttGroupDtos = jyGroupSortCrossDetailDao.queryCommonCTTGroupData(entity);
        } else {
            cttGroupDtos = jyGroupSortCrossDetailDao.queryCommonCTTGroupData(entity);
        }
        resp.setCttGroupDtolist(cttGroupDtos);
        return resp;
    }

    @Override
    public boolean addCTTGroup(AddCTTReq request) {
        List<JyGroupSortCrossDetailEntity> entities = getJyGroupSortCrossDetailEntityList(request.getTemplateCode(), request.getTemplateName(), request, request.getTableTrolleyDtoList());
        return this.batchAddGroup(entities);
    }

    private boolean batchAddGroup(List<JyGroupSortCrossDetailEntity> entities) {
        try {
            if (jyGroupSortCrossDetailDao.batchInsert(entities) > 0) {
                return true;
            } else {
                log.error("新增流向失败：{}", JsonHelper.toJson(entities));
                throw new JyBizException("新增流向失败！");
            }
        } catch (Exception e) {
            log.error("新增流向异常: {}{}", JsonHelper.toJson(entities), e);
            throw new JyBizException("新增流向异常！");
        }
    }

    @Override
    public boolean removeCTTFromGroup(RemoveCTTReq request) {
        // 校验是否包含当前流向
        JyCTTGroupUpdateReq updateReq = new JyCTTGroupUpdateReq();
        List<Long> ids = new ArrayList<>();
        for (TableTrolleyDto tableTrolleyDto : request.getTableTrolleyDtoList()) {
            JyGroupSortCrossDetailEntity query = new JyGroupSortCrossDetailEntity();
            query.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
            query.setGroupCode(request.getGroupCode());
            query.setTemplateCode(request.getTemplateCode());
            query.setEndSiteId(tableTrolleyDto.getEndSiteId().longValue());
            query.setFuncType(SEND_SEAL_DMS.getCode());
            JyGroupSortCrossDetailEntity entity = jyGroupSortCrossDetailDao.selectOneByFlowAndTemplateCode(query);
            if (entity == null) {
                log.error("未查询到该流向：{}", JsonHelper.toJson(query));
                return false;
            }
            ids.add(entity.getId());
        }
        
        try {
            if (jyGroupSortCrossDetailDao.deleteByIds(assembleUpdateReq(request.getUser(),ids)) > 0) {
                return true;
            } else {
                log.error("删除本场地常用的笼车集合失败：{}", JsonHelper.toJson(ids));
                return false;
            }
        } catch (Exception e) {
            log.error("删除本场地常用的笼车集合失败: {}{}", JsonHelper.toJson(ids), e);
            return false;
        }
    }

    @Override
    public List<JyGroupSortCrossDetailEntity> listSendFlowByTemplateCodeOrEndSiteCode(
        JyGroupSortCrossDetailEntity record) {
        return jyGroupSortCrossDetailDao.listSendFlowByTemplateCodeOrEndSiteCode(record);
    }

    @Override
    public CTTGroupDataResp listGroupByEndSiteCodeOrCTTCode(JyGroupSortCrossDetailEntity entity) {
        CTTGroupDataResp resp = new CTTGroupDataResp();
        List<CTTGroupDto> cttGroupDtos = jyGroupSortCrossDetailDao.listGroupByEndSiteCodeOrCTTCode(entity);
        
        if (CollectionUtils.isEmpty(cttGroupDtos)) {
            return resp;
        }
        // 查询混扫任务统计信息
        List<String> templateCodeList = new ArrayList<>();
        for (CTTGroupDto cttGroupDto : cttGroupDtos) {
            templateCodeList.add(cttGroupDto.getTemplateCode());
        }
        JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
        condition.setTemplateCodeList(templateCodeList);
        condition.setGroupCode(entity.getGroupCode());
        List<CTTGroupDto> countList = jyGroupSortCrossDetailDao.listCountByTemplateCode(condition);
        HashMap<String,Integer> countMap = getCountMapByCTTGroupDto(countList);
        cttGroupDtos.forEach(item -> item.setSendFlowCount(countMap.get(item.getTemplateCode())));
        
        log.info("混扫任务信息：{}", JsonHelper.toJson(cttGroupDtos));
        resp.setCttGroupDtolist(cttGroupDtos);
        return resp;
    }

    private HashMap<String, Integer> getCountMapByCTTGroupDto(List<CTTGroupDto> countList) {
        HashMap<String, Integer> map = new HashMap<>();
        for (CTTGroupDto cttGroupDto : countList) {
            map.put(cttGroupDto.getTemplateCode(),cttGroupDto.getSendFlowCount());
        }
        return map;
    }

    @Override
    public JyGroupSortCrossDetailEntity selectOneByFlowAndTemplateCode(JyGroupSortCrossDetailEntity query) {
        return jyGroupSortCrossDetailDao.selectOneByFlowAndTemplateCode(query);
    }

    @Override
    public boolean deleteByIds(JyCTTGroupUpdateReq jyCTTGroupUpdateReq) {
        return jyGroupSortCrossDetailDao.deleteByIds(jyCTTGroupUpdateReq) > 0;
    }

    @Override
    public String getMixScanTaskDefaultName(String defaultPrefix) {
        return String.format(defaultPrefix, this.genObjectId.getObjectId(JyGroupSortCrossDetailEntity.class.getName()));
    }

    @Override
    public boolean deleteMixScanTask(DeleteMixScanTaskReq request) {
        // 获取混扫任务下的流向信息
        JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
        condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        condition.setTemplateCode(request.getTemplateCode());
        condition.setGroupCode(request.getGroupCode());
        List<JyGroupSortCrossDetailEntity> entityList = jyGroupSortCrossDetailDao.listSendFlowByTemplateCodeOrEndSiteCode(condition);

        if (CollectionUtils.isEmpty(entityList)) {
            throw new JyBizException("任务已删除,请刷新页面！");
        }

        List<Long> ids = new ArrayList<>();
        for (JyGroupSortCrossDetailEntity entity : entityList) {
            ids.add(entity.getId());
        }
        return jyGroupSortCrossDetailDao.deleteByIds(assembleUpdateReq(request.getUser(),ids)) > 0;   
    }

    @Override
    public boolean removeMixScanTaskFlow(RemoveMixScanTaskFlowReq request) {
        // 校验是否包含当前流向
        List<Long> ids = new ArrayList<>();
        JyGroupSortCrossDetailEntity entity = jyGroupSortCrossDetailDao.selectOneByFlowAndTemplateCode(assembleFlowReq(request));
        
        if (entity == null) {
            throw new JyBizException("该流向已被移除，请刷新页面");
        }
        
        ids.add(entity.getId());
        return jyGroupSortCrossDetailDao.deleteByIds(assembleUpdateReq(request.getUser(),ids)) > 0;
    }

    @Override
    public CTTGroupDataResp getMixScanTaskListPage(JyGroupSortCrossDetailEntity entity) {
        CTTGroupDataResp resp = new CTTGroupDataResp();
        List<CTTGroupDto> cttGroupDtos = jyGroupSortCrossDetailDao.queryCommonCTTGroupData(entity);
        resp.setCttGroupDtolist(cttGroupDtos);
        return resp;
    }

    @Override
    public boolean mixScanTaskFocus(MixScanTaskFocusReq mixScanTaskFocusReq) {

        JyGroupSortCrossDetailEntity entity = jyGroupSortCrossDetailDao.selectOneByFlowAndTemplateCode(assembleFocusReq(mixScanTaskFocusReq));

        if (entity == null) {
            throw new JyBizException("该流向已被移除，请刷新页面");
        }
        if (entity.getFocus().equals(mixScanTaskFocusReq.getFocus())) {
            return true;
        }
        
        JyGroupSortCrossDetailEntity updateData = new JyGroupSortCrossDetailEntity();
        updateData.setId(entity.getId());
        updateData.setFocus(mixScanTaskFocusReq.getFocus());
        return jyGroupSortCrossDetailDao.updateByPrimaryKeySelective(updateData) > 0;
    }

    @Override
    public String createMixScanTask(CreateMixScanTaskReq request) {
        String templateCode = getTemplateCode();
        List<JyGroupSortCrossDetailEntity> entities = getMixScanTaskList(templateCode, request);
        if (this.createCTTGroup(entities)) {
            return templateCode;
        }
        return null;
    }

    private List<JyGroupSortCrossDetailEntity> getMixScanTaskList(String templateCode, CreateMixScanTaskReq request) {
        List<JyGroupSortCrossDetailEntity> list = request.getSendFlowList().stream().map(item -> {
            JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
            entity.setSendVehicleDetailBizId(item.getSendVehicleDetailBizId());
            entity.setGroupCode(request.getGroupCode());
            entity.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
            entity.setStartSiteName(request.getCurrentOperate().getSiteName());
            entity.setEndSiteId(item.getEndSiteId());
            entity.setEndSiteName(item.getEndSiteName());
            entity.setTabletrolleyCode(item.getTabletrolleyCode());
            entity.setCrossCode(item.getCrossCode());
            entity.setCreateUserName(request.getUser().getUserName());
            entity.setCreateUserErp(request.getUser().getUserErp());
            entity.setCreateTime(new Date());
            entity.setFuncType(SEND_SEAL_WAREHOUSE.getCode());
            entity.setTemplateName(request.getTemplateName());
            entity.setFocus(FocusEnum.FOCUS.getCode());
            entity.setTemplateCode(templateCode);
            return entity;
        }).collect(Collectors.toList());
        return list;
    }

    private JyGroupSortCrossDetailEntity assembleFocusReq(MixScanTaskFocusReq request) {
        JyGroupSortCrossDetailEntity codition = new JyGroupSortCrossDetailEntity();
        codition.setTemplateCode(request.getTemplateCode());
        codition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        codition.setEndSiteId(request.getEndSiteId().longValue());
        codition.setGroupCode(request.getGroupCode());
        codition.setSendVehicleDetailBizId(request.getSendVehicleDetailBizId());
        codition.setFuncType(SEND_SEAL_WAREHOUSE.getCode());
        return codition;
    }

    private JyCTTGroupUpdateReq assembleUpdateReq(User user, List<Long> ids) {
        JyCTTGroupUpdateReq updateReq = new JyCTTGroupUpdateReq();
        updateReq.setIds(ids);
        updateReq.setUpdateUserErp(user.getUserErp());
        updateReq.setUpdateUserName(user.getUserName());
        updateReq.setUpdateTime(new Date());
        return updateReq;
    }

    private JyGroupSortCrossDetailEntity assembleFlowReq(RemoveMixScanTaskFlowReq request) {
        JyGroupSortCrossDetailEntity codition = new JyGroupSortCrossDetailEntity();
        codition.setTemplateCode(request.getTemplateCode());
        codition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        codition.setEndSiteId(request.getEndSiteId().longValue());
        codition.setGroupCode(request.getGroupCode());
        codition.setSendVehicleDetailBizId(request.getSendVehicleDetailBizId());
        codition.setFuncType(SEND_SEAL_WAREHOUSE.getCode());
        return codition;
    }

    @Override
    public Boolean isMixScanProcess(JyGroupSortCrossDetailEntity jyGroupSortCrossDetailEntity) {
        List<JyGroupSortCrossDetailEntity> groupSortCrossDetailEntityList = jyGroupSortCrossDetailDao
                .listSendFlowByTemplateCodeOrEndSiteCode(jyGroupSortCrossDetailEntity);
        if(CollectionUtils.isEmpty(groupSortCrossDetailEntityList)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean appendMixScanTaskFlow(AppendMixScanTaskFlowReq appendMixScanTaskFlowReq) {
        List<JyGroupSortCrossDetailEntity> entities = getAppendMixScanTaskFlowList(appendMixScanTaskFlowReq);
        return this.batchAddGroup(entities);
    }

    
    private List<JyGroupSortCrossDetailEntity> getAppendMixScanTaskFlowList(AppendMixScanTaskFlowReq request) {
        List<JyGroupSortCrossDetailEntity> list = request.getSendFlowList().stream().map(item -> {
            JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
            entity.setSendVehicleDetailBizId(item.getSendVehicleDetailBizId());
            entity.setGroupCode(request.getGroupCode());
            entity.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
            entity.setStartSiteName(request.getCurrentOperate().getSiteName());
            entity.setEndSiteId(item.getEndSiteId());
            entity.setEndSiteName(item.getEndSiteName());
            entity.setTabletrolleyCode(item.getTabletrolleyCode());
            entity.setCrossCode(item.getCrossCode());
            entity.setCreateUserName(request.getUser().getUserName());
            entity.setCreateUserErp(request.getUser().getUserErp());
            entity.setCreateTime(new Date());
            entity.setFuncType(SEND_SEAL_WAREHOUSE.getCode());
            entity.setTemplateName(request.getTemplateName());
            entity.setFocus(FocusEnum.FOCUS.getCode());
            entity.setTemplateCode(request.getTemplateCode());
            return entity;
        }).collect(Collectors.toList());
        return list;
    }


}
