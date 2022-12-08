package com.jd.bluedragon.distribution.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.device.IDeviceLocationExceptionOpLogManager;
import com.jd.bluedragon.core.jsf.device.IDeviceLocationLogManager;
import com.jd.bluedragon.core.jsf.itBasic.ItBasicIpRegionManager;
import com.jd.bluedragon.core.jsf.itBasic.ItBasicRegionManager;
import com.jd.bluedragon.core.jsf.wlLbs.manager.WlLbsApiManager;
import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.api.request.client.DeviceInfo;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationInfo;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationUploadPo;
import com.jd.bluedragon.distribution.device.service.DeviceLocationService;
import com.jd.bluedragon.sdk.modules.device.domain.DeviceLocationLog;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 设备位置网关服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-20 19:59:10 周日
 */
@Service
public class DeviceLocationServiceImpl implements DeviceLocationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ItBasicRegionManager itBasicRegionManager;

    @Resource
    private ItBasicIpRegionManager itBasicIpRegionManager;

    @Resource
    private IDeviceLocationLogManager iDeviceLocationLogManager;

    @Resource
    private IDeviceLocationExceptionOpLogManager iDeviceLocationExceptionOpLogManager;

    @Resource
    private WlLbsApiManager wlLbsApiManager;

    @Autowired
    @Qualifier("dmsUploadDeviceLocationProducer")
    private DefaultJMQProducer dmsUploadDeviceLocationProducer;

    @Autowired
    @Qualifier("dmsUploadDeviceLocationExceptionOpProducer")
    private DefaultJMQProducer dmsUploadDeviceLocationExceptionOpProducer;

    /**
     * 上传设备位置信息
     *
     * @param deviceLocationUploadPo 位置信息
     * @return 上传结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "DeviceLocationServiceImpl.uploadLocationInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<Boolean> uploadLocationInfo(DeviceLocationUploadPo deviceLocationUploadPo) {
        log.info("DeviceLocationServiceImpl.queryCount param {}", JSON.toJSONString(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            // 0. base param check
            // 1. insert location data
            final DeviceLocationLog deviceLocationLog = this.genDeviceLocationLog(deviceLocationUploadPo);
            final Result<Long> remoteResult = iDeviceLocationLogManager.add(deviceLocationLog);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("插入失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(true);
            }
            // 2. check location
            // 2.1 if location is wrong, do some process logic
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.queryCount exception {}", JSON.toJSONString(deviceLocationUploadPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private DeviceLocationLog genDeviceLocationLog(DeviceLocationUploadPo deviceLocationUploadPo) {
        final DeviceLocationLog deviceLocationLog = new DeviceLocationLog();
        // 设备信息
        final DeviceInfo deviceInfo = deviceLocationUploadPo.getDeviceInfo();
        deviceLocationLog.setSystemCode("DMS");
        deviceLocationLog.setDeviceCode(deviceInfo.getDeviceCode());
        deviceLocationLog.setDeviceName(deviceInfo.getDeviceName());
        deviceLocationLog.setDeviceSn(deviceInfo.getDeviceSn());
        deviceLocationLog.setDeviceType(deviceInfo.getDeviceType());
        deviceLocationLog.setProgramType(deviceInfo.getProgramType());
        deviceLocationLog.setVersionCode(deviceInfo.getVersionCode());
        // 操作人信息
        final OperateUser operateUser = deviceLocationUploadPo.getOperateUser();
        deviceLocationLog.setOrgId(operateUser.getOrgId());
        deviceLocationLog.setOrgName(operateUser.getOrgName());
        deviceLocationLog.setSiteCode(operateUser.getSiteCode());
        deviceLocationLog.setSiteName(operateUser.getSiteName());
        deviceLocationLog.setOperateUserErp(operateUser.getUserCode());
        deviceLocationLog.setOperateUserName(operateUser.getUserName());
        // 位置信息
        final DeviceLocationInfo deviceLocationInfo = deviceLocationUploadPo.getDeviceLocationInfo();
        deviceLocationLog.setIpv4(deviceLocationInfo.getIpv4());
        deviceLocationLog.setIpv6(deviceLocationInfo.getIpv6());
        deviceLocationLog.setMacAddressSelf(deviceLocationInfo.getMacAddressSelf());
        deviceLocationLog.setMacAddressNetwork(deviceLocationInfo.getMacAddressNetwork());
        deviceLocationLog.setLongitude(deviceLocationInfo.getLongitude());
        deviceLocationLog.setLatitude(deviceLocationInfo.getLatitude());

        deviceLocationLog.setOperateTime(new Date(deviceLocationUploadPo.getOperateTime()));
        deviceLocationLog.setCreateTime(new Date());
        return deviceLocationLog;
    }

    /**
     * 检查用户位置是否与其所属站点匹配
     *
     * @param deviceLocationUploadPo 上传信息
     * @return 匹配结果
     * @author fanggang7
     * @time 2022-12-07 14:31:32 周三
     */
    @Override
    public Result<Boolean> checkLocationMatchUserSite(DeviceLocationUploadPo deviceLocationUploadPo) {
        log.info("DeviceLocationServiceImpl.checkLocationMatchUserSite param {}", JSON.toJSONString(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            ;
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.checkLocationMatchUserSite exception {}", JSON.toJSONString(deviceLocationUploadPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 上传异常位置操作数据
     *
     * @param deviceLocationUploadPo 上传信息
     * @return 匹配结果
     * @author fanggang7
     * @time 2022-12-07 14:31:32 周三
     */
    @Override
    public Result<Boolean> uploadLocationExceptionOpInfo(DeviceLocationUploadPo deviceLocationUploadPo) {
        log.info("DeviceLocationServiceImpl.uploadLocationExceptionOpInfo param {}", JSON.toJSONString(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            ;
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.uploadLocationExceptionOpInfo exception {}", JSON.toJSONString(deviceLocationUploadPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 设备位置上传消息处理
     *
     * @param deviceLocationUploadPo 上传信息
     * @return 匹配结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    @Override
    public Result<Boolean> handleDmsUploadDeviceLocationConsume(DeviceLocationUploadPo deviceLocationUploadPo) {
        log.info("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationConsume param {}", JSON.toJSONString(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            ;
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationConsume exception {}", JSON.toJSONString(deviceLocationUploadPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 设备操作位置异常上传消息处理
     *
     * @param deviceLocationUploadPo 上传信息
     * @return 匹配结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    @Override
    public Result<Boolean> handleDmsUploadDeviceLocationExceptionOpConsume(DeviceLocationUploadPo deviceLocationUploadPo) {
        log.info("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationExceptionOpConsume param {}", JSON.toJSONString(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            ;
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationExceptionOpConsume exception {}", JSON.toJSONString(deviceLocationUploadPo), e);
            result.toFail("系统异常");
        }
        return result;
    }
}
