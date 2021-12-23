package com.jd.bluedragon.distribution.discardedPackageStorageTemp.handler;

import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedStorageContext;

/**
 * 暂存弃件处理接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021年12月06日11:16:053 周一
 */
public interface DiscardedStorageHandler {

    /**
     * 暂存弃件处理
     * @param context 上下文
     * @return 处理结果
     * @author fanggang7
     * @time 2021-12-06 11:17:30 周一
     */
    Result<Boolean> handle(DiscardedStorageContext context);
}
