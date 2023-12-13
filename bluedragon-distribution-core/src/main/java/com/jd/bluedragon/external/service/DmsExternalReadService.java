package com.jd.bluedragon.external.service;

import com.jd.bluedragon.distribution.api.response.box.BoxDto;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 分拣额外只读服务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-12-09 15:40:10 周六
 */
public interface DmsExternalReadService {

    /**
     * 根据包裹号查询箱号数据
     * @param packageCode 包裹号
     * @return 箱号数据
     * @author fanggang7
     * @time 2023-12-09 15:30:44 周六
     */
    Result<BoxDto> getBoxInfoByPackageCode(String packageCode);
}
