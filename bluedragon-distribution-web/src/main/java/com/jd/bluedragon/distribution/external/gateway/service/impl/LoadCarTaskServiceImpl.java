package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.CreateLoadTaskReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadCarTaskCreateReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadTaskListReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.common.dto.unloadCar.HelperDto;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.enums.LicenseNumberAreaCodeEnum;
import com.jd.bluedragon.external.gateway.service.LoadCarTaskService;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
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
    private LoadCarDao loadCarDao;

    @Autowired
    BaseMajorManager baseMajorManager;


    @Override
    public JdCResponse startTask(CreateLoadTaskReq req) {


        return null;
    }

    @Override
    public JdCResponse deleteLoadCarTask(LoadDeleteReq req) {
        loadCarDao.deleteById(req);
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

    /**
     * 车牌转换
     *
     * @param licenseNumber
     * @return
     */
    @Override
    public JdCResponse<String> checkLicenseNumber(String licenseNumber) {
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        if (StringUtils.isBlank(licenseNumber)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("车牌号不能为空");
            return jdCResponse;
        }
        if (licenseNumber.length() == 9) {
            for (LicenseNumberAreaCodeEnum licenseNumberAreaCodeEnum : LicenseNumberAreaCodeEnum.values()) {
                if (licenseNumberAreaCodeEnum.getAreaName().equals(licenseNumber.substring(0, 3))) {
                    licenseNumber = licenseNumber.replace(licenseNumber.substring(0, 3), licenseNumberAreaCodeEnum.getAreaName());
                    jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
                    jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
                    jdCResponse.setData(licenseNumber);
                    break;
                }
            }
        }
        return jdCResponse;
    }

    /**
     * 任务列表接口
     *
     * @param req
     * @return
     */
    @Override
    public JdCResponse<List<LoadTaskListDto>> loadCarTaskList(LoadTaskListReq req) {
        JdCResponse<List<LoadTaskListDto>> jdCResponse = new JdCResponse<>();
        if (null == req || StringUtils.isBlank(req.getLoginUserErp())) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("当前登录人信息为空");
            return jdCResponse;
        }
        List<LoadTaskListDto> taskList = loadCarDao.queryByErp(req.getLoginUserErp());
        jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        jdCResponse.setData(CollectionUtils.isEmpty(taskList) ? new ArrayList<LoadTaskListDto>() : taskList);
        return jdCResponse;
    }

    /**
     * 装车任务创建
     *
     * @param req
     * @return
     */
    @Override
    public JdCResponse<Long> loadCarTaskCreate(LoadCarTaskCreateReq req) {
        JdCResponse<Long> jdCResponse = new JdCResponse<>();
        jdCResponse.setCode(JdCResponse.CODE_ERROR);
        if (null == req) {
            jdCResponse.setMessage("请求参数不能为空");
        }
        LoadCar loadCar=new LoadCar();
        BeanUtils.copyProperties(req,loadCar);
        loadCar.setCreateTime(new Date());
        loadCar.setOperateTime(new Date());
        loadCar.setStatus(0);
        return null;
    }

    /**
     * 根据erp获取员工信息
     *
     * @param erp
     * @return
     */
    @Override
    public JdCResponse<HelperDto> getNameByErp(String erp) {
        JdCResponse<HelperDto> jdCResponse = new JdCResponse<>();
        BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(erp);
        log.info("装车添加协助人-获取员工信息接口响应结果={}", JSON.toJSONString(bssod));
        if (null == bssod) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("员工信息不存在");
            return jdCResponse;
        }
        HelperDto helperDto = new HelperDto();
        helperDto.setHelperERP(bssod.getErp());
        helperDto.setHelperName(bssod.getStaffName());
        jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        jdCResponse.setData(helperDto);
        return jdCResponse;
    }
}
