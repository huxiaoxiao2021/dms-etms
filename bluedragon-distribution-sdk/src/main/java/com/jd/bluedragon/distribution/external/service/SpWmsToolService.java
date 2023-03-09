package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.external.domain.SpWmsCreateInRequest;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/2/17
 * @Description:  备件库小工具
 */
public interface SpWmsToolService {

    /**
     * 虚拟操作创建备件库入库单
     * @param request
     * @return
     */
    InvokeResult<Boolean> virtualSpWmsCreateIn(SpWmsCreateInRequest request);


    /**
     * 批量虚拟操作创建备件库入库单
     * @param request
     * @return
     */
    InvokeResult<List<String>> batchVirtualSpWmsCreateIn(List<SpWmsCreateInRequest> request);

}
