package com.jd.bluedragon.distribution.jy.service.comboard.impl;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.comboard.request.AddCTTReq;
import com.jd.bluedragon.common.dto.comboard.request.CTTGroupDataReq;
import com.jd.bluedragon.common.dto.comboard.request.CreateGroupCTTReq;
import com.jd.bluedragon.common.dto.comboard.request.RemoveCTTReq;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDataResp;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDto;
import com.jd.bluedragon.common.dto.comboard.response.CreateGroupCTTResp;
import com.jd.bluedragon.common.dto.comboard.response.TableTrolleyDto;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyGroupSortCrossDetailDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyCTTGroupUpdateReq;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static final String COM_BOARD_SEND = "CTT%s";

    @Override
    public CreateGroupCTTResp batchInsert(CreateGroupCTTReq request) {
        CreateGroupCTTResp resp = new CreateGroupCTTResp();
        String templateCode = getTemplateCode();
        List<JyGroupSortCrossDetailEntity> jyGroupSortCrossDetailEntityList = getJyGroupSortCrossDetailEntityList(templateCode, request.getTemplateName(), request, request.getTableTrolleyDtoList());
        try {
            if (jyGroupSortCrossDetailDao.batchInsert(jyGroupSortCrossDetailEntityList) > 0) {
                resp.setTemplateCode(templateCode);
                return resp;
            } else {
                log.error("保存本场地常用的笼车集合失败：{}", JsonHelper.toJson(jyGroupSortCrossDetailEntityList));
                return null;
            }
        } catch (Exception e) {
            log.error("保存本场地常用的笼车集合失败: {}{}", JsonHelper.toJson(jyGroupSortCrossDetailEntityList), e);
            return null;
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
        try {
            if (jyGroupSortCrossDetailDao.batchInsert(entities) > 0) {
                return true;
            } else {
                log.error("新增本场地常用的笼车集合失败：{}", JsonHelper.toJson(entities));
                return false;
            }
        } catch (Exception e) {
            log.error("新增本场地常用的笼车集合失败: {}{}", JsonHelper.toJson(entities), e);
            return false;
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
            JyGroupSortCrossDetailEntity entity = jyGroupSortCrossDetailDao.selectOneByGroupCrossTableTrolley(query);
            if (entity == null) {
                log.error("未查询到该流向：{}", JsonHelper.toJson(query));
                return false;
            }
            ids.add(entity.getId());
        }
        
        updateReq.setIds(ids);
        updateReq.setUpdateUserErp(request.getUser().getUserErp());
        updateReq.setUpdateUserName(request.getUser().getUserName());
        updateReq.setUpdateTime(new Date());
        try {
            if (jyGroupSortCrossDetailDao.deleteByIds(updateReq) > 0) {
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
        List<CTTGroupDto> cttGroupDtos = jyGroupSortCrossDetailDao.listCTTGroupData(entity);
        log.info("混扫任务信息：{}", JsonHelper.toJson(cttGroupDtos));
        resp.setCttGroupDtolist(cttGroupDtos);
        return resp;
    }

    @Override
    public JyGroupSortCrossDetailEntity selectOneByGroupCrossTableTrolley(JyGroupSortCrossDetailEntity query) {
        return jyGroupSortCrossDetailDao.selectOneByGroupCrossTableTrolley(query);
    }

    @Override
    public boolean deleteByIds(JyCTTGroupUpdateReq jyCTTGroupUpdateReq) {
        return jyGroupSortCrossDetailDao.deleteByIds(jyCTTGroupUpdateReq) > 0;
    }
}
