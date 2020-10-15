package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.CreateLoadTaskReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadCarTaskCreateReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadTaskListReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.external.gateway.service.LoadCarTaskService;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2020-10-15 14:09
 */
public class LoadCarTaskServiceImpl implements LoadCarTaskService {

    private Logger log = LoggerFactory.getLogger(LoadCarTaskServiceImpl.class);

    @Autowired
    BaseMajorManager baseMajorManager;


    @Override
    public JdCResponse startTask(CreateLoadTaskReq req) {
        return null;
    }

    @Override
    public JdCResponse deleteLoadCarTask(LoadDeleteReq req) {
        return null;
    }

    /**
     * 根据目的地Id获取名称
     *
     * @param endSiteCode
     * @return
     */
    @Override
    public JdCResponse<String> getEndSiteName(Long endSiteCode) {
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        BaseStaffSiteOrgDto toSite = baseMajorManager.getBaseSiteBySiteId(endSiteCode.intValue());
        if (null == toSite) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("目的站点不存在");
            return jdCResponse;
        }
        jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        return jdCResponse;
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

    /**
     * 根据erp获取员工信息
     *
     * @param erp
     * @return
     */
    @Override
    public JdCResponse<String> getNameByErp(String erp) {
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(erp);
        log.info("装车添加协助人-获取员工信息接口响应结果={}", JSON.toJSONString(bssod));
        if (null == bssod) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("员工信息不存在");
            return jdCResponse;
        }
        jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        jdCResponse.setData(bssod.getStaffName());
        return jdCResponse;
    }
}
