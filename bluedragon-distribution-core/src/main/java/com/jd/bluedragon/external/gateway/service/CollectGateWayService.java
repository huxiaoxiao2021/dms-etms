package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.collect.request.CollectGoodsRequest;
import com.jd.bluedragon.common.dto.collect.response.CollectGoodsResponse;
import java.util.List;

/**
 * 安卓集货接口服务类
 */
public interface CollectGateWayService {

    /**
     * 获取某个分拣中心下的所有集货区
     * @param request
     * @return
     */
    JdCResponse<List<CollectGoodsResponse>> findAreas(CollectGoodsRequest request);

    /**
     * 集货  上架存放接口
     * 自动计算暂存位 并存放记录
     * 未验货时自动触发验货
     * @param request
     * @return
     */
    JdVerifyResponse<CollectGoodsResponse> collectPut(CollectGoodsRequest request);

    /**
     * 获取已扫数据
     * 输入包裹号 返回包裹号对应的运单所在集货位的所有数据
     * 输入货位号 返回货位全部数据
     * @param request
     * @return
     */
    JdCResponse<CollectGoodsResponse> findScanInfo(CollectGoodsRequest request);

    /**
     * 集货  异常转移接口
     * 转移集货位 或 包裹 至异常集货位
     * @param request
     * @return
     */
    JdCResponse<Boolean> transfer(CollectGoodsRequest request);

    /**
     * 集货  释放集货位接口
     * 释放集货位 或 集货区所有数据
     * @param request
     * @return
     */
    JdCResponse<Boolean> clean(CollectGoodsRequest request);
}
