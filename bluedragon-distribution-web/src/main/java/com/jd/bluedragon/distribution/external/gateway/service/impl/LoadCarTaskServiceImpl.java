package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.CreateLoadTaskReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadTaskListReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.external.gateway.service.LoadCarTaskService;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2020-10-15 14:09
 */
public class LoadCarTaskServiceImpl implements LoadCarTaskService {
    @Override
    public JdCResponse createLoadCarTask(CreateLoadTaskReq createLoadTaskReq) {
        return null;
    }

    @Override
    public JdCResponse deleteLoadCarTask(LoadDeleteReq loadDeleteReq) {
        return null;
    }

    @Override
    public JdCResponse getEndSiteName(Long endSiteCode) {
        return null;
    }

    @Override
    public JdCResponse checkLicenseNumber(String licenseNumber) {
        return null;
    }

    @Override
    public JdCResponse<List<LoadTaskListDto>> loadCarTaskList(LoadTaskListReq loadTaskListReq) {
        return null;
    }
}
