package com.jd.bluedragon.distribution.jy.service.comboard.impl;

import com.jd.bluedragon.common.dto.comboard.request.CTTGroupDataReq;
import com.jd.bluedragon.common.dto.comboard.request.CreateGroupCTTReq;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDataResp;
import com.jd.bluedragon.common.dto.comboard.response.CreateGroupCTTResp;
import com.jd.bluedragon.common.dto.comboard.response.TableTrolleyDto;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyGroupSortCrossDetailDao;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jim.cli.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.utils.TimeUtils.yyyyMMdd;

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
        List<JyGroupSortCrossDetailEntity> jyGroupSortCrossDetailEntityList = new ArrayList<>();
        for (TableTrolleyDto tableTrolleyDto : request.getTableTrolleyDtoList()) {
            JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
            entity.setGroupCode(request.getGroupCode());
            entity.setTemplateCode(templateCode);
            entity.setTemplateName(request.getTemplateName());
            entity.setCreateTime(new Date());
            entity.setCreateUserErp(request.getUser().getUserErp());
            entity.setCreateUserName(request.getUser().getUserName());
            entity.setCrossCode(tableTrolleyDto.getCrossCode());
            entity.setEndSiteId(tableTrolleyDto.getEndSiteId().longValue());
            entity.setEndSiteName(tableTrolleyDto.getEndSiteName());
            entity.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
            entity.setStartSiteName(request.getCurrentOperate().getSiteName());
            entity.setTabletrolleyCode(tableTrolleyDto.getTableTrolleyCode());
            jyGroupSortCrossDetailEntityList.add(entity);
        }
        try{
            if ( jyGroupSortCrossDetailDao.batchInsert(jyGroupSortCrossDetailEntityList) >0 ) {
                resp.setTemplateCode(templateCode);
                return resp;
            } else {
                log.error("保存本场地常用的笼车集合失败：{}",JsonHelper.toJson(jyGroupSortCrossDetailEntityList));
                return null;
            }
        } catch (Exception e) {
            log.error("保存本场地常用的笼车集合失败: {}{}",JsonHelper.toJson(jyGroupSortCrossDetailEntityList),e);
            return null;
        }
    }
    private String getTemplateCode() {
        String key = String.format(COM_BOARD_SEND, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return key + StringHelper.padZero(redisJyBizIdSequenceGen.gen(key));
    }
    
    @Override
    public CTTGroupDataResp queryCTTGroupDataByGroupOrSiteCode(CTTGroupDataReq request) {
        return null;
    }
}
