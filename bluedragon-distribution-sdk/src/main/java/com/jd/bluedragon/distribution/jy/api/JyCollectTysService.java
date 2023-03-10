package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportReqDto;
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
     * @param collectReportReqDto
     * @return
     */
    InvokeResult<CollectReportResDto> findCollectInfo(CollectReportReqDto collectReportReqDto);

    /**
     * 查询集齐明细（查包裹）
     * @param collectReportReqDto
     * @return
     */
    InvokeResult<CollectReportDetailResDto> findCollectDetail(CollectReportReqDto collectReportReqDto);


}
