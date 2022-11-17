package com.jd.bluedragon.distribution.jy.service.comboard;

import com.jd.bluedragon.common.dto.comboard.request.CTTGroupDataReq;
import com.jd.bluedragon.common.dto.comboard.request.CreateGroupCTTReq;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDataResp;
import com.jd.bluedragon.common.dto.comboard.response.CreateGroupCTTResp;

/**
 * @author liwenji
 * @date 2022-11-17 18:21
 */
public interface JyGroupSortCrossDetailService {
    CreateGroupCTTResp batchInsert(CreateGroupCTTReq request);

    CTTGroupDataResp queryCTTGroupDataByGroupOrSiteCode(CTTGroupDataReq request);
}
