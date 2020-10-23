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
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadCarHelperService;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadService;
import com.jd.bluedragon.external.gateway.service.LoadCarTaskGateWayService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.jd.bluedragon.enums.LicenseNumberAreaCodeEnum.transferLicenseNumber;
import static com.jd.bluedragon.utils.DateHelper.daysDiff;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2020-10-15 14:09
 */
public class LoadCarTaskGateWayServiceImpl implements LoadCarTaskGateWayService {

    private Logger log = LoggerFactory.getLogger(LoadCarTaskGateWayServiceImpl.class);

    @Autowired
    private LoadService loadService;

    @Autowired
    private LoadCarHelperService loadCarHelperService;

    @Autowired
    BaseMajorManager baseMajorManager;

    /**
     * 添加装车任务协助人
     *
     * @param req
     * @return
     */
    @JProfiler(jKey = "DMSWEB.LoadCarTaskGateWayServiceImpl.startTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse startTask(CreateLoadTaskReq req) {
        JdCResponse jdCResponse = new JdCResponse();
        if (null == req) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("添加协助人信息不完整,请检查必填信息！");
            return jdCResponse;
        }
        List<HelperDto> helperList = req.getAssistorInfo();
        if (CollectionUtils.isEmpty(helperList)) {
            jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
            jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
            return jdCResponse;
        }
        List<LoadCarHelper> list = Lists.newArrayListWithExpectedSize(helperList.size());
        for (HelperDto helperDto : helperList) {
            LoadCarHelper loadCarHelper = new LoadCarHelper();
            loadCarHelper.setCreateSiteName(req.getCreateSiteName());
            loadCarHelper.setCreateSiteCode(req.getCreateSiteCode());
            loadCarHelper.setTaskId(req.getId());
            loadCarHelper.setCreateUserErp(req.getCreateUserErp());
            loadCarHelper.setCreateUserName(req.getCreateUserName());
            loadCarHelper.setHelperErp(helperDto.getHelperERP());
            loadCarHelper.setHelperName(helperDto.getHelperName());
            list.add(loadCarHelper);
        }
        loadCarHelperService.batchInsert(list);
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
    @JProfiler(jKey = "DMSWEB.LoadCarTaskGateWayServiceImpl.deleteLoadCarTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse deleteLoadCarTask(LoadDeleteReq req) {
        JdCResponse jdCResponse = new JdCResponse();
        if (null == req || null == req.getId()) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("接口请求信息不完整,请联系IT");
            return jdCResponse;
        }
        if (loadService.deleteById(req) > 0) {
            loadCarHelperService.deleteById(req.getId());
            jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
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
    @JProfiler(jKey = "DMSWEB.LoadCarTaskGateWayServiceImpl.getEndSiteName",
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
    @JProfiler(jKey = "DMSWEB.LoadCarTaskGateWayServiceImpl.checkLicenseNumber",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> checkLicenseNumber(String licenseNumber) {
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        if (StringUtils.isBlank(licenseNumber)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("车牌号不合规,请检查后重试");
            return jdCResponse;
        }
        if (licenseNumber.length() == 9) {
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
    @JProfiler(jKey = "DMSWEB.LoadCarTaskGateWayServiceImpl.loadCarTaskList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<LoadTaskListDto>> loadCarTaskList(LoadTaskListReq req) {
        JdCResponse<List<LoadTaskListDto>> jdCResponse = new JdCResponse<>();
        if (null == req || StringUtils.isBlank(req.getLoginUserErp())) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("当前登录人信息为空！");
            return jdCResponse;
        }
        List<Long> creatorList = loadCarHelperService.selectByCreateUserErp(req.getLoginUserErp());
        List<Long> helperList = loadCarHelperService.selectByHelperErp(req.getLoginUserErp());
        List<Long> loadCarList = loadService.selectByCreateUserErp(req.getLoginUserErp());
        List<LoadTaskListDto> taskListDto = new ArrayList<>();
        if (CollectionUtils.isEmpty(creatorList) && CollectionUtils.isEmpty(helperList) && CollectionUtils.isEmpty(loadCarList)) {
            jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
            jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
            jdCResponse.setData(taskListDto);
            return jdCResponse;
        }
        List<Long> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(creatorList)) {
            taskIds.addAll(creatorList);
        }
        if (CollectionUtils.isNotEmpty(helperList)) {
            taskIds.addAll(helperList);
        }
        if (CollectionUtils.isNotEmpty(loadCarList)) {
            taskIds.addAll(loadCarList);
        }
        if (CollectionUtils.isEmpty(taskIds)) {
            jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
            jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
            jdCResponse.setData(taskListDto);
            return jdCResponse;
        }
        Set<Long> set = new HashSet<>(taskIds);
        taskIds.clear();
        taskIds.addAll(set);
        taskListDto = loadService.selectByIds(taskIds);
        jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        jdCResponse.setData(taskListDto);
        return jdCResponse;
    }

    /**
     * 装车任务创建
     *
     * @param req
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.LoadCarTaskGateWayServiceImpl.loadCarTaskCreate",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Long> loadCarTaskCreate(LoadCarTaskCreateReq req) {
        JdCResponse<Long> jdCResponse = new JdCResponse<>();
        try {
            if (null == req) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("装车任务信息不完整,请检查必填信息！");
                return jdCResponse;
            }
            if (null == req.getCreateUserErp() || StringUtils.isBlank(req.getCreateUserName())) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("当前登录人信息为空,请重新登录！");
                return jdCResponse;
            }
            if (null == req.getEndSiteCode() || StringUtils.isBlank(req.getEndSiteName())) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("目的地站点编码或名称不能为空！");
                return jdCResponse;
            }
            if (null == req.getCreateSiteCode() || StringUtils.isBlank(req.getCreateSiteName())) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("当前登录人所属站点信息不存在,请稍后重试！");
                return jdCResponse;
            }
            LoadCar loadCar = new LoadCar();
            loadCar.setCreateSiteCode(req.getCreateSiteCode());
            loadCar.setEndSiteCode(req.getEndSiteCode());
            List<LoadCar> taskList = loadService.selectByEndSiteCode(loadCar);
            Date now = new Date();
            //库中如果存在
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (LoadCar taskInfo : taskList) {
                    //判断是否有3天还没结束的任务,有的话直接删除任务
                    if (daysDiff(taskInfo.getUpdateTime(), now) >= 3) {
                        loadCarHelperService.deleteById(taskInfo.getId());
                        LoadDeleteReq loadDeleteReq = new LoadDeleteReq();
                        loadDeleteReq.setOperateUserErp(req.getCreateUserErp());
                        loadDeleteReq.setOperateUserName(req.getCreateUserName());
                        loadDeleteReq.setId(taskInfo.getId());
                        loadService.deleteById(loadDeleteReq);
                    } else {
                        jdCResponse.setCode(JdCResponse.CODE_ERROR);
                        jdCResponse.setMessage("同一个转运中心，一个目的场地只能创建一个进行中任务！");
                        return jdCResponse;
                    }
                }
            }
            BeanUtils.copyProperties(req, loadCar);
            loadCar.setCreateTime(new Date());
            loadCar.setUpdateTime(new Date());
            loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BLANK);
            loadCar.setOperateUserErp(req.getCreateUserErp());
            loadCar.setOperateUserName(req.getCreateUserName());
            int id = loadService.insert(loadCar);
            if (id > 0) {
                jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
                jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
                jdCResponse.setData(loadCar.getId());
                return jdCResponse;
            }
        } catch (Exception e) {
            log.error("装卸任务创建请求异常={}", e);
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
    @JProfiler(jKey = "DMSWEB.LoadCarTaskGateWayServiceImpl.getNameByErp",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<HelperDto> getNameByErp(String erp) {
        JdCResponse<HelperDto> jdCResponse = new JdCResponse<>();
        if (StringUtils.isBlank(erp)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("erp信息不能为空！");
            return jdCResponse;
        }
        BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(erp);
        if (null == bssod || StringUtils.isBlank(bssod.getStaffName())) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("暂未查询到员工姓名,稍后请重试！");
            return jdCResponse;
        }
        HelperDto helperDto = new HelperDto();
        helperDto.setHelperERP(erp);
        helperDto.setHelperName(bssod.getStaffName());
        jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        jdCResponse.setData(helperDto);
        return jdCResponse;
    }

}
