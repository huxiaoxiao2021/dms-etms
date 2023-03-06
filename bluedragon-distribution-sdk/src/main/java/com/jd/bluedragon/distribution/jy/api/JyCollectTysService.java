package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectQueryReqDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportDetailResDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportResDto;

/**
 * @Author zhengchengfa
 * @Description //转运集齐服务
 * @date
 **/
public interface JyCollectTysService {

    /**
     * 查询集齐列表
     * @param collectQueryReqDto
     * @return
     */
    InvokeResult<CollectReportResDto> findCollectInfo(CollectQueryReqDto collectQueryReqDto);

    /**
     * 查询集齐明细（查包裹）
     * @param collectQueryReqDto
     * @return
     */
    InvokeResult<CollectReportDetailResDto> findCollectDetail(CollectQueryReqDto collectQueryReqDto);


}
