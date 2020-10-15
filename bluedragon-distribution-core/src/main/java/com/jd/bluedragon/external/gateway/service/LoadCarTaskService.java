package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.CreateLoadTaskReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadTaskListReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;

import java.util.List;


/**
 * @program: bluedragon-distribution
 * @description: 装车任务相关
 * @author: wuming
 * @create: 2020-10-15 13:37
 */
public interface LoadCarTaskService {

    /**
     * 创建转装车任务接口
     *
     * @param createLoadTaskReq
     * @return
     */
    JdCResponse createLoadCarTask(CreateLoadTaskReq createLoadTaskReq);

    /**
     * 删除装车任务接口
     *
     * @param loadDeleteReq
     * @return
     */
    JdCResponse deleteLoadCarTask(LoadDeleteReq loadDeleteReq);

    /**
     * 转运中心
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
     * @param loadTaskListReq
     * @return
     */
    JdCResponse<List<LoadTaskListDto>> loadCarTaskList(LoadTaskListReq loadTaskListReq);

}
