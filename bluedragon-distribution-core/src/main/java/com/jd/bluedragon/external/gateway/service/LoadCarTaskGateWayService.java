package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.CreateLoadTaskReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadCarTaskCreateReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadTaskListReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.common.dto.unloadCar.HelperDto;

import java.util.List;


/**
 * @program: bluedragon-distribution
 * @description: 装车任务相关
 * @author: wuming
 * @create: 2020-10-15 13:37
 */
public interface LoadCarTaskGateWayService {

    /**
     * 创建装车任务接口
     *
     * @param req
     * @return
     */
    JdCResponse startTask(CreateLoadTaskReq req);

    /**
     * 删除装车任务接口
     *
     * @param req
     * @return
     */
    JdCResponse deleteLoadCarTask(LoadDeleteReq req);

    /**
     * 获取转运中心名称
     *
     * @param endSiteCode
     * @return
     */
    JdCResponse<String> getEndSiteName(Long endSiteCode);

    /**
     * 车牌号转换
     *
     * @param licenseNumber
     * @return
     */
    JdCResponse<String> checkLicenseNumber(String licenseNumber);

    /**
     * 装卸任务列表
     *
     * @param req
     * @return
     */
    JdCResponse<List<LoadTaskListDto>> loadCarTaskList(LoadTaskListReq req);


    /**
     * 装车任务创建,返回任务Id
     *
     * @param req
     * @return
     */
    JdCResponse<Long> loadCarTaskCreate(LoadCarTaskCreateReq req);

    /**
     * 根据erp获取姓名
     *
     * @param erp
     * @return
     */
    JdCResponse<HelperDto> getNameByErp(String erp);
}
