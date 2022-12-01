package com.jd.bluedragon.distribution.jy.service.comboard;

import com.jd.bluedragon.common.dto.comboard.request.AddCTTReq;
import com.jd.bluedragon.common.dto.comboard.request.CTTGroupDataReq;
import com.jd.bluedragon.common.dto.comboard.request.CreateGroupCTTReq;
import com.jd.bluedragon.common.dto.comboard.request.RemoveCTTReq;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDataResp;
import com.jd.bluedragon.common.dto.comboard.response.CreateGroupCTTResp;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import java.util.List;

/**
 * @author liwenji
 * @date 2022-11-17 18:21
 */
public interface JyGroupSortCrossDetailService {

    /**
     * 批量新增
     * @param request
     * @return
     */
    CreateGroupCTTResp batchInsert(CreateGroupCTTReq request);

    /**
     * 查询(本岗位或本场地)常用滑道笼车流向集合
     * @param request
     * @return
     */
    CTTGroupDataResp queryCTTGroupDataByGroupOrSiteCode(CTTGroupDataReq request);

    /**
     * 更新流向
     * @param request
     * @return
     */
    boolean addCTTGroup(AddCTTReq request);

    /**
     * 移除流向
     * @param request
     * @return
     */
    boolean removeCTTFromGroup(RemoveCTTReq request);

    /**
     *
     * @param record
     * @return
     */
    List<JyGroupSortCrossDetailEntity> listSendFlowByTemplateCode(JyGroupSortCrossDetailEntity record);

    /**
     * 根据目的地或CTTcode获取混扫任务信息
     * @param entity
     * @return
     */
    CTTGroupDataResp listGroupByEndSiteCodeOrCTTCode(JyGroupSortCrossDetailEntity entity);

    /**
     * 查询流向
     * @param query
     * @return
     */
    JyGroupSortCrossDetailEntity selectOneByGroupCrossTableTrolley(JyGroupSortCrossDetailEntity query);
}
