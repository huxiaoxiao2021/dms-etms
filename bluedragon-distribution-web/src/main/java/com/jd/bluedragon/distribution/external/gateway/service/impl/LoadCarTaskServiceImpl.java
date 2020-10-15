package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.CreateLoadTaskReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadCarTaskCreateReq;
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
    public JdCResponse startTask(CreateLoadTaskReq req) {
        return null;
    }

    @Override
    public JdCResponse deleteLoadCarTask(LoadDeleteReq req) {
        return null;
    }

    @Override
    public JdCResponse<String> getEndSiteName(Long endSiteCode) {
        return null;
    }

    @Override
    public JdCResponse<String> checkLicenseNumber(String licenseNumber) {
        return null;
    }

    @Override
    public JdCResponse<List<LoadTaskListDto>> loadCarTaskList(LoadTaskListReq req) {
        return null;
    }

    @Override
    public JdCResponse<Long> loadCarTaskCreate(LoadCarTaskCreateReq req) {
        return null;
    }

    @Override
    public JdCResponse<String> getNameByErp(String erp) {
        return null;
    }
}
