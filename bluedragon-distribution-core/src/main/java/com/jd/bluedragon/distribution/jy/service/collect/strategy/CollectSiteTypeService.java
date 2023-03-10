package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectDto;

/**
 * @Author zhengchengfa
 * @Description // 集齐类型服务   策略类（仅实现，不做策略方法）
 * @date
 **/
@Deprecated
public interface CollectSiteTypeService {

    /**
     * 初始化保存集齐数据(封车、无任务扫描)
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
