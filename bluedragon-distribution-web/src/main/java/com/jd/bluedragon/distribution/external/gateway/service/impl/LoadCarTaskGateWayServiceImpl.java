package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.CreateLoadTaskReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadCarTaskCreateReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadTaskListReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.BasicDictDataDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadCarInfoDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.common.dto.unloadCar.HelperDto;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanCacheService;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCarHelper;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadCarHelperService;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadService;
import com.jd.bluedragon.enums.CarTypeEnum;
import com.jd.bluedragon.external.gateway.service.LoadCarTaskGateWayService;
import com.alibaba.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
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
import static com.jd.bluedragon.enums.LicenseNumberAreaCodeEnum.transferLicenseNumber2;
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

    @Autowired
    private LoadScanCacheService loadScanCacheService;

    @Autowired
    private LoadScanService loadScanService;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private BasicQueryWSManager basicQueryWSManager;

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
            if (StringUtils.equals(req.getCreateUserErp(), helperDto.getHelperERP())) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("装车协助人不允许添加本任务的负责人！");
                return jdCResponse;
            }
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
        LoadCar lc = loadService.findLoadCarById(req.getId());
        if (null == lc) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("该任务不存在!");
            return jdCResponse;
        }
        if (GoodsLoadScanConstants.YN_N.equals(lc.getYn())) {
            jdCResponse.setCode(JdCResponse.CODE_FAIL);
            jdCResponse.setMessage("该任务已被删除,不可重复操作!");
            return jdCResponse;
        }
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN.equals(lc.getStatus())) {
            jdCResponse.setCode(JdCResponse.CODE_FAIL);
            jdCResponse.setMessage("该任务已开始,无法删除");
            return jdCResponse;
        } else if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(lc.getStatus())) {
            jdCResponse.setCode(JdCResponse.CODE_FAIL);
            jdCResponse.setMessage("该任务已完成,无法删除");
            return jdCResponse;
        }
        //校验删除任务人的权限
        if (!loadCarHelperService.checkUserPermission(req.getId(), req.getOperateUserErp())) {
            jdCResponse.setCode(JdCResponse.CODE_FAIL);
            jdCResponse.setMessage("非任务创建人或协助人不可删除任务！");
            return jdCResponse;
        }
        loadScanCacheService.delTaskLoadScan(req.getId());
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
        boolean flag = false;
        /**针对010A77777长度9**/
        if (licenseNumber.length() == 9) {
            String temp = licenseNumber;
            licenseNumber = transferLicenseNumber(licenseNumber);
            flag = temp.equals(licenseNumber);
        }
        /**针对0371A77777长度10**/
        if (licenseNumber.length() == 10) {
            String temp = licenseNumber;//如果是数字，转化成功存在汉字，两者不相等，如果相等就不正常
            licenseNumber = transferLicenseNumber2(licenseNumber);
            flag = temp.equals(licenseNumber);
        }
        if (flag) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("车牌号不合规,请检查后重试");
            return jdCResponse;
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
        if (log.isDebugEnabled()) {
            log.debug("装车任务创建接口请求参数={}", JSON.toJSONString(req));
        }
        JdCResponse<Long> jdCResponse = new JdCResponse<>();
        try {
            if (null == req) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("装车任务信息不完整,请检查必填信息！");
                return jdCResponse;
            }
            if (StringUtils.isBlank(req.getCreateUserErp()) || StringUtils.isBlank(req.getCreateUserName())) {
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
            loadCar.setLicenseNumber(req.getLicenseNumber());
            loadCar.setWeight(convertWeightUnitToRequired(req.getWeight()));
            loadCar.setVolume(convertVolumeUnit(req.getVolume()));
            List<LoadCar> taskList = loadService.selectByEndSiteCode(loadCar);
            Date now = new Date();
            //库中如果存在
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (LoadCar taskInfo : taskList) {
                    if (daysDiff(taskInfo.getUpdateTime(), now) < 3) {
                        jdCResponse.setCode(JdCResponse.CODE_ERROR);
                        jdCResponse.setMessage("相同流向相同车辆只能创建一个任务！");
                        return jdCResponse;
                    }
                    //判断是否有3天还没结束的任务,有的话直接删除任务
                    loadCarHelperService.deleteById(taskInfo.getId());
                    LoadDeleteReq loadDeleteReq = new LoadDeleteReq();
                    loadDeleteReq.setOperateUserErp(req.getCreateUserErp());
                    loadDeleteReq.setOperateUserName(req.getCreateUserName());
                    loadDeleteReq.setId(taskInfo.getId());
                    loadService.deleteById(loadDeleteReq);
                    loadScanCacheService.delTaskLoadScan(taskInfo.getId());
                    // 删除已开始任务时，删除关联的运单信息和包裹信息
                    if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN.equals(taskInfo.getStatus())) {
                        loadScanService.deleteLoadScanByTaskId(taskInfo.getId());
                    }
                }
            }
            BeanUtils.copyProperties(req, loadCar);
            Double weight = req.getWeight() == null ? null : this.convertWeightUnitToRequired(req.getWeight());
            Double volume = req.getVolume() == null ? null : this.convertVolumeUnit(req.getVolume());
            log.info("转换后重量={},体积={}", weight, volume);
            loadCar.setWeight(weight);
            loadCar.setVolume(volume);
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

    /**
     * -1:全部站点隐藏;0打开;1隐藏
     *
     * @param currentSiteCode
     * @return
     */
    @Override
    public JdCResponse<Integer> hideMenuCheck(Integer currentSiteCode) {
        JdCResponse<Integer> jdCResponse = new JdCResponse<>();
        if (null == currentSiteCode) {
            jdCResponse.toFail("操作站点编号不能为空");
            return jdCResponse;
        }
        String sites = dmsConfigManager.getUccPropertyConfig().getDeliverHideSites();
        log.info("发货菜单隐藏配置信息={}", sites);
        if (StringUtils.isBlank(sites)) {
            jdCResponse.toSucceed();
            jdCResponse.setData(Constants.SITE_OPEN);
            return jdCResponse;
        }
        List<String> list = Arrays.asList(sites.split(";"));
        //关闭站点配置里包含当前站点，需要隐藏
        if (list.contains(String.valueOf(currentSiteCode))) {
            jdCResponse.toSucceed();
            jdCResponse.setData(Constants.SITE_CLOSE);
            return jdCResponse;
        }
        if (Constants.CLOSE_ALL_SITE.equals(sites)) {
            jdCResponse.toSucceed();
            jdCResponse.setData(Constants.ALL_SITE_CLOSE);
            return jdCResponse;
        }
        jdCResponse.toSucceed();
        jdCResponse.setData(Constants.SITE_OPEN);
        return jdCResponse;
    }

    /**
     * 根据车牌获取车辆信息
     *
     * @param vehicleNum
     * @return
     */
    @Override
    public JdCResponse<LoadCarInfoDto> getCarInfoByVehicleNum(String vehicleNum) {
        JdCResponse jdCResponse = new JdCResponse();
        if (StringUtils.isBlank(vehicleNum)) {
            jdCResponse.toFail("车牌号不能为空");
            return jdCResponse;
        }
        boolean flag = false;
        if (vehicleNum.length() == Constants.VEHICLE_NUMBER_LENGTH_9) {
            String temp = vehicleNum;
            vehicleNum = transferLicenseNumber(vehicleNum);
            flag = temp.equals(vehicleNum);
        }
        if (vehicleNum.length() == Constants.VEHICLE_NUMBER_LENGTH_10) {
            String temp = vehicleNum;
            vehicleNum = transferLicenseNumber2(vehicleNum);
            flag = temp.equals(vehicleNum);
        }
        if (flag) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("车牌号不合规,请检查后重试");
            return jdCResponse;
        }
        BasicVehicleTypeDto dto = basicQueryWSManager.getVehicleTypeByVehicleNum(vehicleNum);
        if (null == dto) {
            jdCResponse.toFail("获取车辆配置信息失败");
            return jdCResponse;
        }
        LoadCarInfoDto loadCarInfoDto = new LoadCarInfoDto();
        loadCarInfoDto.setVehicleTypeName(dto.getVehicleTypeName());
        loadCarInfoDto.setVolume(dto.getVolume());
        loadCarInfoDto.setWeight(dto.getWeight());
        jdCResponse.toSucceed();
        jdCResponse.setData(loadCarInfoDto);
        return jdCResponse;
    }

    /**
     * @return
     */
    @Override
    public JdCResponse<List<BasicDictDataDto>> getCarList() {
        JdCResponse<List<BasicDictDataDto>> jdCResponse = new JdCResponse<>();
        List<BasicDictDataDto> list = new ArrayList<>();
        List<com.jd.tms.basic.dto.BasicDictDto> result = basicQueryWSManager.getDictList(Constants.PARENT_CODE, Constants.DICT_LEVEL, Constants.DICT_GROUP);
        if (CollectionUtils.isEmpty(result)) {
            jdCResponse.toFail("获取车辆信息异常");
            return jdCResponse;
        }
        for (com.jd.tms.basic.dto.BasicDictDto basicDictDto : result) {
            BasicDictDataDto dataDto = new BasicDictDataDto();
            BeanUtils.copyProperties(basicDictDto, dataDto);
            list.add(dataDto);
        }
        jdCResponse.toSucceed();
        jdCResponse.setData(list);
        return jdCResponse;
    }

    @Override
    public JdCResponse<LoadCarInfoDto> getCarInfoByType(Integer vehicleType) {
        JdCResponse<LoadCarInfoDto> jdCResponse = new JdCResponse<>();
        BasicVehicleTypeDto dto = basicQueryWSManager.getVehicleTypeByVehicleType(vehicleType);
        if (null == dto) {
            jdCResponse.toFail("查询操作异常");
            return jdCResponse;
        }
        LoadCarInfoDto loadCarInfoDto = new LoadCarInfoDto();
        loadCarInfoDto.setVehicleTypeName(dto.getVehicleTypeName());
        loadCarInfoDto.setVolume(dto.getVolume());
        loadCarInfoDto.setVehicleTypeName(dto.getVehicleTypeName());
        loadCarInfoDto.setWeight(dto.getWeight());
        jdCResponse.setData(loadCarInfoDto);
        jdCResponse.toSucceed();
        return jdCResponse;
    }

    /**
     * 获取默认车型列表信息
     *
     * @return
     */
    @Override
    public JdCResponse<List<LoadCarInfoDto>> getDefaultCarList() {
        JdCResponse<List<LoadCarInfoDto>> jdCResponse = new JdCResponse<>();
        List<LoadCarInfoDto> carInfoDtoList = CarTypeEnum.getCarTypeList();
        jdCResponse.toSucceed("操作成功！");
        jdCResponse.setData(carInfoDtoList);
        return jdCResponse;
    }


    /**
     * 吨转kg
     *
     * @param weight 吨
     * @return kg
     */
    private Double convertWeightUnitToRequired(Double weight) {
        if (null == weight) {
            return null;
        }
        return weight * 1000.0;
    }

    /**
     * 体积单位 传入值为立方米 运单要求标准为立方厘米
     *
     * @param volume 立方米
     * @return 体积 立方厘米
     */
    private Double convertVolumeUnit(Double volume) {
        if (null == volume) {
            return null;
        }
        return volume * 1000000.0;
    }

}
