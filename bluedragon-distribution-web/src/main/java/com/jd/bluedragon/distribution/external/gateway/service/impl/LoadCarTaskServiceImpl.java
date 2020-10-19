package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.CreateLoadTaskReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadCarTaskCreateReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadTaskListReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.common.dto.unloadCar.HelperDto;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCarHelper;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarHelperDao;
import com.jd.bluedragon.enums.LicenseNumberAreaCodeEnum;
import com.jd.bluedragon.external.gateway.service.LoadCarTaskService;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.enums.LicenseNumberAreaCodeEnum.transferLicenseNumber;

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
    private LoadCarHelperDao loadCarHelperDao;

    @Autowired
    BaseMajorManager baseMajorManager;

    /**
     * 添加装车任务协助人
     *
     * @param req
     * @return
     */
    @JProfiler(jKey = "DMSWEB.LoadCarTaskServiceImpl.startTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse startTask(CreateLoadTaskReq req) {
        JdCResponse jdCResponse = new JdCResponse();
        if (null == req || CollectionUtils.isEmpty(req.getAssistorInfo())) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("添加协助人信息不完整,请检查必填信息！");
        }
        List<HelperDto> helperList = req.getAssistorInfo();
        LoadCarHelper loadCarHelper = new LoadCarHelper();
        List<LoadCarHelper> list = Lists.newArrayListWithExpectedSize(helperList.size());
        loadCarHelper.setTaskId(req.getId());
        loadCarHelper.setCreateTime(new Date());
        loadCarHelper.setCreateTime(new Date());
        for (HelperDto helperDto : helperList) {
            loadCarHelper.setCreateUserErp(req.getCreateUserErp());
            loadCarHelper.setCreateUserName(req.getCreateUserName());
            loadCarHelper.setHelperErp(helperDto.getHelperERP());
            loadCarHelper.setHelperName(helperDto.getHelperName());
            list.add(loadCarHelper);
        }
        loadCarHelperDao.batchInsert(list);
        jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        return jdCResponse;
    }

    /**
     * 删除任务列表任务
     *
     * @param req
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.LoadCarTaskServiceImpl.deleteLoadCarTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse deleteLoadCarTask(LoadDeleteReq req) {
        JdCResponse jdCResponse = new JdCResponse();
        jdCResponse.setData(JdCResponse.CODE_ERROR);
        jdCResponse.setMessage("删除任务失败,稍后请重试");
        if (loadCarDao.deleteById(req) > 0) {
            jdCResponse.setData(JdCResponse.CODE_SUCCESS);
            jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        }
        return jdCResponse;
    }

    /**
     * 根据目的地Id获取名称
     *
     * @param endSiteCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.LoadCarTaskServiceImpl.getEndSiteName",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.LoadCarTaskServiceImpl.checkLicenseNumber",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> checkLicenseNumber(String licenseNumber) {
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        if (StringUtils.isBlank(licenseNumber)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("车牌号不能为空");
            return jdCResponse;
        }
        if (licenseNumber.trim().length() == 9) {
            licenseNumber = transferLicenseNumber(licenseNumber);
        }
        jdCResponse.setData(licenseNumber);
        jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        return jdCResponse;
    }

    /**
     * 任务列表接口
     *
     * @param req
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.LoadCarTaskServiceImpl.loadCarTaskList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<LoadTaskListDto>> loadCarTaskList(LoadTaskListReq req) {
        JdCResponse<List<LoadTaskListDto>> jdCResponse = new JdCResponse<>();
        if (null == req || StringUtils.isBlank(req.getLoginUserErp())) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("当前登录人信息为空！");
            return jdCResponse;
        }
        List<Long>taskIds=loadCarHelperDao.selectTasksByErp(req.getLoginUserErp());






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
    @JProfiler(jKey = "DMSWEB.LoadCarTaskServiceImpl.loadCarTaskCreate",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Long> loadCarTaskCreate(LoadCarTaskCreateReq req) {
        JdCResponse<Long> jdCResponse = new JdCResponse<>();
        if (null == req) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("装车任务信息不完整,请检查必填信息！");
            return jdCResponse;
        }
        LoadCar loadCar = new LoadCar();
        BeanUtils.copyProperties(req, loadCar);
        loadCar.setCreateTime(new Date());
        loadCar.setUpdateTime(new Date());
        loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BLANK);
        int id = loadCarDao.insert(loadCar);
        if (id > 0) {
            jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
            jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
            jdCResponse.setData((long) id);
        }
        jdCResponse.setCode(JdCResponse.CODE_ERROR);
        jdCResponse.setMessage("操作失败,请稍后重试！");
        return jdCResponse;
    }

    /**
     * 根据erp获取员工信息
     *
     * @param erp
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.LoadCarTaskServiceImpl.getNameByErp",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<HelperDto> getNameByErp(String erp) {
        JdCResponse<HelperDto> jdCResponse = new JdCResponse<>();
        BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(erp);
        log.info("装车添加协助人-获取员工信息接口响应结果={}", JSON.toJSONString(bssod));
        if (null == bssod || StringUtils.isBlank(bssod.getStaffName())) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("暂未查询到员工姓名,稍后请重试！");
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
