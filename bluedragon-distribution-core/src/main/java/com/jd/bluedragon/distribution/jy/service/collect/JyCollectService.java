package com.jd.bluedragon.distribution.jy.service.collect;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectQueryReqDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportDetailResDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportResDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectDto;

/**
 * @Author zhengchengfa
 * @Description ////集齐服务
 * @date
 **/
public interface JyCollectService {


    /**
     * 查询集齐统计信息
     * @param collectQueryReqDto
     * @return
     */
    InvokeResult<CollectReportResDto> findCollectInfo(CollectQueryReqDto collectQueryReqDto);

    /**
     * 查询集齐明细信息
     * @param collectQueryReqDto
     * @return
     */
    InvokeResult<CollectReportDetailResDto> findCollectDetail(CollectQueryReqDto collectQueryReqDto);

    /**
     * 解析该场地为运单的中转场地还是末端场地
     * @param scanCode
     * @param siteCode
     * @return CollectSiteTypeEnum
     */
    Integer parseSiteType(String scanCode, Integer siteCode);


    /**
     * 初始化保存集齐数据（封车、无任务扫描）
     * @param collectDto
     * @return
     */
    InvokeResult initCollect(CollectDto collectDto);
    /**
     * 消除集齐数据（取消封车）
     * @param collectDto
     * @return
     */
    InvokeResult removeCollect(CollectDto collectDto);

}
