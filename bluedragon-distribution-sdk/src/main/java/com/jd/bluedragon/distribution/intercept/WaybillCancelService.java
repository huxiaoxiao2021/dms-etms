package com.jd.bluedragon.distribution.intercept;

import com.jd.dms.java.utils.sdk.base.Result;

import java.util.List;

/**
 * 运单拦截接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-02-10 16:38:54 周五
 */
public interface WaybillCancelService {

    /**
     * 删除异常运单拦截
     * @param waybillCodeList 运单单号列表
     * @return 处理结果
     * @author fanggang7
     * @time 2023-02-10 17:38:52 周五
     */
    Result<Integer> delByWaybillCodeListInterceptType99(List<String> waybillCodeList);
}
