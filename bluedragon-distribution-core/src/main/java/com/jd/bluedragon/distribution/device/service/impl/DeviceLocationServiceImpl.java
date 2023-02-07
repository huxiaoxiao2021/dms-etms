package com.jd.bluedragon.distribution.device.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.device.IDeviceLocationExceptionOpLogManager;
import com.jd.bluedragon.core.jsf.device.IDeviceLocationLogManager;
import com.jd.bluedragon.core.jsf.itBasic.ItBasicIpRegionManager;
import com.jd.bluedragon.core.jsf.itBasic.ItBasicRegionManager;
import com.jd.bluedragon.core.jsf.wlLbs.dto.fence.FenceData;
import com.jd.bluedragon.core.jsf.wlLbs.dto.fence.TransFenceInfoVo;
import com.jd.bluedragon.core.jsf.wlLbs.dto.fenceForWeb.Geometry;
import com.jd.bluedragon.core.jsf.wlLbs.dto.qo.QueryTransFenceBySiteIdQo;
import com.jd.bluedragon.core.jsf.wlLbs.manager.WlLbsApiWrapResultManager;
import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.api.request.client.DeviceInfo;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationInfo;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationUploadPo;
import com.jd.bluedragon.distribution.device.service.DeviceLocationService;
import com.jd.bluedragon.sdk.modules.device.domain.DeviceLocationExceptionOpLog;
import com.jd.bluedragon.sdk.modules.device.domain.DeviceLocationLog;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.location.GeoUtils;
import com.jd.bluedragon.utils.location.GeometryUtil;
import com.jd.bluedragon.utils.location.dto.LatLng;
import com.jd.dms.java.utils.core.common.IpUtils;
import com.jd.dms.java.utils.core.gis.Coordinate;
import com.jd.dms.java.utils.core.gis.CoordinateTransformUtil;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.lbs.jdlbsapi.dto.LocationRequestDto;
import com.jd.lbs.jdlbsapi.dto.LocationResultDto;
import com.jd.lbs.jdlbsapi.dto.drawtool.PointDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.dto.itBasic.dto.MatchIpRegionDto;
import com.jdl.basic.api.dto.itBasic.po.ItBasicIpRegionPo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private WlLbsApiWrapResultManager wlLbsApiWrapResultManager;

    @Autowired
    @Qualifier("dmsUploadDeviceLocationProducer")
    private DefaultJMQProducer dmsUploadDeviceLocationProducer;

    @Autowired
    @Qualifier("dmsUploadDeviceLocationExceptionOpProducer")
    private DefaultJMQProducer dmsUploadDeviceLocationExceptionOpProducer;

    /**
     * 上传设备位置信息消息
     *
     * @param deviceLocationUploadPo 位置信息
     * @return 上传结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    @Override
    public Result<Boolean> sendUploadLocationMsg(DeviceLocationUploadPo deviceLocationUploadPo) {
        log.info("DeviceLocationServiceImpl.sendUploadLocationMsg param {}", JsonHelper.toJson(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> checkResult = this.checkParam4UploadLocation(deviceLocationUploadPo);
            if (!checkResult.isSuccess()) {
                log.warn("DeviceLocationServiceImpl.sendUploadLocationMsg invalid param {} {}", JsonHelper.toJson(checkResult), JsonHelper.toJson(deviceLocationUploadPo));
                return result.toFail(checkResult.getMessage());
            }
            String businessId = deviceLocationUploadPo.getOperateUser().getUserCode();
            dmsUploadDeviceLocationProducer.send(businessId, JsonHelper.toJson(deviceLocationUploadPo));
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.sendUploadLocationMsg exception {}", JsonHelper.toJson(deviceLocationUploadPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Boolean> checkParam4UploadLocation(DeviceLocationUploadPo deviceLocationUploadPo) {
        Result<Boolean> result = Result.success();
        if (deviceLocationUploadPo == null) {
            return result.toFail("参数错误，参数不能为空");
        }
        final OperateUser operateUser = deviceLocationUploadPo.getOperateUser();
        if (operateUser == null) {
            return result.toFail("参数错误，operateUser不能为空");
        }
        if (operateUser.getUserCode() == null) {
            return result.toFail("参数错误，userCode不能为空");
        }
        if (operateUser.getSiteCode() == null) {
            return result.toFail("参数错误，siteCode不能为空");
        }
        if (operateUser.getOrgId() == null) {
            return result.toFail("参数错误，orgId不能为空");
        }
        /*final DeviceLocationInfo deviceLocationInfo = deviceLocationUploadPo.getDeviceLocationInfo();
        if (deviceLocationInfo == null) {
            return result.toFail("参数错误，deviceLocationInfo不能为空");
        }
        if (deviceLocationInfo.getLongitude() == null) {
            return result.toFail("参数错误，longitude不能为空");
        }
        if (deviceLocationInfo.getLatitude() == null) {
            return result.toFail("参数错误，latitude不能为空");
        }*/
        return result;
    }

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
        log.info("DeviceLocationServiceImpl.uploadLocationInfo param {}", JsonHelper.toJson(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            // 0. base param check
            final Result<Boolean> checkResult = this.checkParam4UploadLocation(deviceLocationUploadPo);
            if (!checkResult.isSuccess()) {
                log.warn("DeviceLocationServiceImpl.uploadLocationInfo invalid param {} {}", JsonHelper.toJson(checkResult), JsonHelper.toJson(deviceLocationUploadPo));
                return result.toFail(checkResult.getMessage());
            }
            // 1. insert location data
            final DeviceLocationLog deviceLocationLog = this.genDeviceLocationLog(deviceLocationUploadPo);

            // 2. check location
            final Result<Boolean> checkLocationResult = this.checkLocationMatchUserSite(deviceLocationUploadPo);

            if(!checkLocationResult.isSuccess()){
                log.warn("DeviceLocationServiceImpl.uploadLocationInfo checkLocationMatchUserSite fail {}", JsonHelper.toJson(checkLocationResult));
                // return result;
            } else {
                deviceLocationLog.setInSiteFence(checkLocationResult.getData() ? Constants.YN_YES : Constants.YN_NO);
                deviceLocationLog.setDistanceToSite(deviceLocationUploadPo.getDeviceLocationInfo().getDistanceToSite());
            }
            final Result<Long> remoteResult = iDeviceLocationLogManager.add(deviceLocationLog);
            if (remoteResult == null || remoteResult.isFail()) {
                return result.toFail(String.format("插入失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                deviceLocationLog.setId(remoteResult.getData());
                deviceLocationUploadPo.setRefLogId(remoteResult.getData());
                result.setData(true);
            }
            // 2.1 if location is wrong, do some process logic
            if(!checkLocationResult.getData()){
                this.sendUploadLocationExceptionOpMsg(deviceLocationUploadPo);
            }
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.uploadLocationInfo exception {}", JsonHelper.toJson(deviceLocationUploadPo), e);
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
        if (deviceLocationInfo != null) {
            deviceLocationLog.setIpv4(deviceLocationInfo.getIpv4());
            deviceLocationLog.setIpv6(deviceLocationInfo.getIpv6());
            deviceLocationLog.setMacAddressSelf(deviceLocationInfo.getMacAddressSelf());
            deviceLocationLog.setMacAddressNetwork(deviceLocationInfo.getMacAddressNetwork());
            deviceLocationLog.setLongitude(deviceLocationInfo.getLongitude());
            deviceLocationLog.setLatitude(deviceLocationInfo.getLatitude());
        }

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
        log.info("DeviceLocationServiceImpl.checkLocationMatchUserSite param {}", JsonHelper.toJson(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        result.setData(false);
        try {
            // 1. 查询场地围栏
            final OperateUser operateUser = deviceLocationUploadPo.getOperateUser();
            QueryTransFenceBySiteIdQo queryTransFenceBySiteIdQo = new QueryTransFenceBySiteIdQo();
            queryTransFenceBySiteIdQo.setSiteId(operateUser.getSiteCode());
            final Result<FenceData> queryFenceRespResult = wlLbsApiWrapResultManager.queryTransFenceBySiteIdForWeb(queryTransFenceBySiteIdQo);
            if (!queryFenceRespResult.isSuccess()) {
                log.warn("DeviceLocationServiceImpl.checkLocationMatchUserSite queryTransFenceBySiteId fail {}", JsonHelper.toJson(queryFenceRespResult));
                return result.toFail("处理失败，查询场地围栏信息失败");
            }
            final FenceData fenceData = queryFenceRespResult.getData();
            if (fenceData == null) {
                log.warn("DeviceLocationServiceImpl.checkLocationMatchUserSite queryTransFenceBySiteId empty {}", JsonHelper.toJson(queryFenceRespResult));
                return result.toFail("未查询到围栏数据");
            }
            // 2. 若有经纬度
            boolean hasLocation = false;
            final DeviceLocationInfo deviceLocationInfo = deviceLocationUploadPo.getDeviceLocationInfo();
            if(deviceLocationInfo != null && deviceLocationInfo.getLatitude() != null && deviceLocationInfo.getLongitude() != null){
                hasLocation = true;
            }

            final List<TransFenceInfoVo> transFenceInfoVoList = fenceData.getTransFenceInfoVoList();
            //  2.1 判断设备经纬度是否在场地围栏内
            if (hasLocation) {
                // 有经纬度计算坐标点到围栏中心点的直线距离
                this.calculateSiteDistance(deviceLocationUploadPo, transFenceInfoVoList);
                final Coordinate coordinate = CoordinateTransformUtil.transformWGS84ToGCJ02(deviceLocationInfo.getLatitude().doubleValue(), deviceLocationInfo.getLongitude().doubleValue());
                LatLng point = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
                final boolean isPointInPolygon = checkLatLngPointInFence(point, transFenceInfoVoList);
                log.info("hasLocation checkLatLngPointInFence isPointInPolygon {}", isPointInPolygon);
                if(isPointInPolygon){
                    return result.setData(true);
                }
                // 经纬度可能不准，继续用网络IP形式去加一层判断
                final String ipv4 = deviceLocationInfo.getIpv4();
                if(ipv4 != null){
                    final Result<Boolean> netWorkCheckResult = checkNetworkInFence(ipv4, transFenceInfoVoList);
                    if(!netWorkCheckResult.isSuccess()){
                        return result.toFail(netWorkCheckResult.getMessage());
                    }
                    if(netWorkCheckResult.getData()){
                        return result.setData(true);
                    }
                }
            }
            else {
                // 3. 若没有经纬度
                //  3.1 判断是内网还是外网
                final String ipv4 = deviceLocationInfo.getIpv4();
                if(ipv4 != null) {
                    final Result<Boolean> netWorkCheckResult = checkNetworkInFence(ipv4, transFenceInfoVoList);
                    if (!netWorkCheckResult.isSuccess()) {
                        return result.toFail(netWorkCheckResult.getMessage());
                    }
                    if (netWorkCheckResult.getData()) {
                        return result.setData(true);
                    }
                }
            }
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.checkLocationMatchUserSite exception {}", JsonHelper.toJson(deviceLocationUploadPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Boolean> calculateSiteDistance(DeviceLocationUploadPo deviceLocationUploadPo, List<TransFenceInfoVo> transFenceInfoVoList) {
        Result<Boolean> result = Result.success();
        PointDto startPoint = new PointDto();

        DeviceLocationInfo deviceLocationInfo = deviceLocationUploadPo.getDeviceLocationInfo();
        startPoint.setLat(deviceLocationInfo.getLatitude());
        startPoint.setLng(deviceLocationInfo.getLongitude());

        for (TransFenceInfoVo transFenceInfoVo : transFenceInfoVoList) {
            // 可能存在"主围栏"、"临时围栏"，只要满足一个条件即可
            final Geometry geometry = transFenceInfoVo.getGeometry();
            if (geometry == null) {
                continue;
            }

            // 取重心点，得到距离计算位置点
            final List<List<List<Double>>> coordinates = geometry.getCoordinates();
            if (CollectionUtils.isEmpty(coordinates)) {
                continue;
            }
            List<LatLng> polygonShape = new ArrayList<>();
            final List<List<Double>> pointList = coordinates.get(0);
            if (CollectionUtils.isEmpty(pointList)) {
                continue;
            }
            for (List<Double> doubles : pointList) {
                final LatLng latLng = new LatLng();
                latLng.setLng(doubles.get(0));
                latLng.setLat(doubles.get(1));
                polygonShape.add(latLng);
            }
            final LatLng centerOfGravityPoint = GeometryUtil.getCenterOfGravityPoint(polygonShape);

            PointDto endPoint = new PointDto();
            endPoint.setLat(BigDecimal.valueOf(centerOfGravityPoint.getLat()));
            endPoint.setLng(BigDecimal.valueOf(centerOfGravityPoint.getLng()));

            final Result<BigDecimal> lengthResult = wlLbsApiWrapResultManager.getLength(null, startPoint, endPoint);
            if (lengthResult.isSuccess() && lengthResult.getData() != null) {
                final BigDecimal distance = lengthResult.getData();
                if(deviceLocationInfo.getDistanceToSite() == null){
                    deviceLocationInfo.setDistanceToSite(distance);
                } else {
                    if(deviceLocationInfo.getDistanceToSite().compareTo(distance) > 0){
                        deviceLocationInfo.setDistanceToSite(distance);
                    }
                }
            }
        }
        // log.info("DeviceLocationServiceImpl.calculateSiteDistance deviceLocationUploadPo: {}", JsonHelper.toJson(deviceLocationUploadPo));
        return result;
    }

    /**
     * 判断经纬度点是否在围栏内
     * @param point 经纬度点
     * @param transFenceInfoVoList 围栏列表数据
     * @return 判断结果
     */
    private boolean checkLatLngPointInFence(LatLng point, List<TransFenceInfoVo> transFenceInfoVoList) {
        boolean isPointInPolygon = false;
        for (TransFenceInfoVo transFenceInfoVo : transFenceInfoVoList) {
            // 可能存在"主围栏"、"临时围栏"，只要满足一个条件即可
            final Geometry geometry = transFenceInfoVo.getGeometry();
            if (geometry == null) {
                continue;
            }
            final List<List<List<Double>>> coordinates = geometry.getCoordinates();
            if (CollectionUtils.isEmpty(coordinates)) {
                continue;
            }
            List<LatLng> polygonShape = new ArrayList<>();
            final List<List<Double>> pointList = coordinates.get(0);
            if (CollectionUtils.isEmpty(pointList)) {
                continue;
            }
            for (List<Double> doubles : pointList) {
                final LatLng latLng = new LatLng();
                latLng.setLng(doubles.get(0));
                latLng.setLat(doubles.get(1));
                polygonShape.add(latLng);
            }
            isPointInPolygon = GeoUtils.isPointInPolygon(point, polygonShape);
            // 如果不在围栏内，判断两点之间直线距离
            if (isPointInPolygon) {
                break;
            }
        }
        return isPointInPolygon;
    }

    /**
     * 检查网络IP地址是否在围栏范围内
     * @param ip IP地址
     * @param transFenceInfoVoList 围栏范围
     * @return 判断结果
     */
    private Result<Boolean> checkNetworkInFence(String ip, List<TransFenceInfoVo> transFenceInfoVoList) {
        Result<Boolean> result = Result.success();
        boolean netWorkInFence = false;
        boolean isInternalNetwork = true;
        if (IpUtils.isInternalFormatV4Ip(ip)) {
            isInternalNetwork = false;
        }
        //  3.1 根据IP地址查询场地地址，内网调IT基础接口，外网调gis接口
        if (!isInternalNetwork) {
            final LocationRequestDto locationRequestDto = new LocationRequestDto();
            locationRequestDto.setIp(ip);
            final Result<LocationResultDto> ipLocationResult = wlLbsApiWrapResultManager.getLocationByIp(null, locationRequestDto);
            if (!ipLocationResult.isSuccess()) {
                log.warn("DeviceLocationServiceImpl.checkNetworkInFence getLocationByIp fail {}", JsonHelper.toJson(ipLocationResult));
                 return result.toFail("处理失败，根据内网IP查询位置失败失败");
            }
            final LocationResultDto ipLocationData = ipLocationResult.getData();
            if (ipLocationData == null) {
                log.warn("DeviceLocationServiceImpl.checkNetworkInFence getLocationByIp empty {}", JsonHelper.toJson(ipLocationData));
                 return result.toFail("未获取到内网IP地址数据");
            }
            //  3.2 根据场地地址查询经纬度，在判断经纬度是否在场地围栏内 (不用继续查，3.1已返回经纬度)
            LatLng point = new LatLng(ipLocationData.getLat().doubleValue(), ipLocationData.getLng().doubleValue());
            final boolean isPointInPolygon = checkLatLngPointInFence(point, transFenceInfoVoList);
            result.setData(isPointInPolygon);
        } else {
            // 如果是内网IP，只用判断是在一个城市即可
            ItBasicIpRegionPo itBasicIpRegionPo = new ItBasicIpRegionPo();
            itBasicIpRegionPo.setIp(ip);
            final Result<MatchIpRegionDto> matchIpRegionResult = itBasicIpRegionManager.selectMatchRegionByIp(itBasicIpRegionPo);
            if (!matchIpRegionResult.isSuccess()) {
                log.warn("DeviceLocationServiceImpl.checkNetworkInFence selectMatchRegionByIp fail {}", JsonHelper.toJson(matchIpRegionResult));
                return result.toFail("处理失败，根据内网IP查询位置失败失败");
            }
            final MatchIpRegionDto matchIpRegionDto = matchIpRegionResult.getData();
            if (matchIpRegionDto != null) {
                // 根据地址获取经纬度 todo 园区信息表依据结果较差，只能精确到城市维度
            }
        }
        return result.setData(netWorkInFence);
    }

    /**
     * 上传设备位置信息消息
     *
     * @param deviceLocationUploadPo 位置信息
     * @return 上传结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    @Override
    public Result<Boolean> sendUploadLocationExceptionOpMsg(DeviceLocationUploadPo deviceLocationUploadPo) {
        log.info("DeviceLocationServiceImpl.sendUploadLocationExceptionOpMsg param {}", JsonHelper.toJson(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            // 0. base param check
            final Result<Boolean> checkResult = this.checkParam4UploadLocation(deviceLocationUploadPo);
            if (!checkResult.isSuccess()) {
                log.warn("DeviceLocationServiceImpl.sendUploadLocationExceptionOpMsg invalid param {} {}", JsonHelper.toJson(checkResult), JsonHelper.toJson(deviceLocationUploadPo));
                return result.toFail(checkResult.getMessage());
            }
            String businessId = deviceLocationUploadPo.getOperateUser().getUserCode();
            dmsUploadDeviceLocationExceptionOpProducer.send(businessId, JsonHelper.toJson(deviceLocationUploadPo));
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.sendUploadLocationExceptionOpMsg exception {}", JsonHelper.toJson(deviceLocationUploadPo), e);
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
        log.info("DeviceLocationServiceImpl.uploadLocationExceptionOpInfo param {}", JsonHelper.toJson(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            // 0. base param check
            final Result<Boolean> checkResult = this.checkParam4UploadLocation(deviceLocationUploadPo);
            if (!checkResult.isSuccess()) {
                log.warn("DeviceLocationServiceImpl.uploadLocationInfo invalid param {} {}", JsonHelper.toJson(checkResult), JsonHelper.toJson(deviceLocationUploadPo));
                return result.toFail(checkResult.getMessage());
            }
            // 1. insert location data
            final DeviceLocationExceptionOpLog deviceLocationExceptionOpLog = this.genDeviceLocationExceptionOpLog(deviceLocationUploadPo);
            final Result<Long> remoteResult = iDeviceLocationExceptionOpLogManager.add(deviceLocationExceptionOpLog);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("插入失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(true);
            }
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.uploadLocationExceptionOpInfo exception {}", JsonHelper.toJson(deviceLocationUploadPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private DeviceLocationExceptionOpLog genDeviceLocationExceptionOpLog(DeviceLocationUploadPo deviceLocationUploadPo) {
        final DeviceLocationExceptionOpLog deviceLocationExceptionOpLog = new DeviceLocationExceptionOpLog();
        deviceLocationExceptionOpLog.setRefLogId(deviceLocationUploadPo.getRefLogId());
        // 设备信息
        final DeviceInfo deviceInfo = deviceLocationUploadPo.getDeviceInfo();
        deviceLocationExceptionOpLog.setSystemCode("DMS");
        deviceLocationExceptionOpLog.setDeviceCode(deviceInfo.getDeviceCode());
        deviceLocationExceptionOpLog.setDeviceName(deviceInfo.getDeviceName());
        deviceLocationExceptionOpLog.setDeviceSn(deviceInfo.getDeviceSn());
        deviceLocationExceptionOpLog.setDeviceType(deviceInfo.getDeviceType());
        deviceLocationExceptionOpLog.setProgramType(deviceInfo.getProgramType());
        deviceLocationExceptionOpLog.setVersionCode(deviceInfo.getVersionCode());
        // 操作人信息
        final OperateUser operateUser = deviceLocationUploadPo.getOperateUser();
        deviceLocationExceptionOpLog.setOrgId(operateUser.getOrgId());
        deviceLocationExceptionOpLog.setOrgName(operateUser.getOrgName());
        deviceLocationExceptionOpLog.setSiteCode(operateUser.getSiteCode());
        deviceLocationExceptionOpLog.setSiteName(operateUser.getSiteName());
        deviceLocationExceptionOpLog.setOperateUserErp(operateUser.getUserCode());
        deviceLocationExceptionOpLog.setOperateUserName(operateUser.getUserName());
        // 位置信息
        final DeviceLocationInfo deviceLocationInfo = deviceLocationUploadPo.getDeviceLocationInfo();
        deviceLocationExceptionOpLog.setIpv4(deviceLocationInfo.getIpv4());
        deviceLocationExceptionOpLog.setIpv6(deviceLocationInfo.getIpv6());
        deviceLocationExceptionOpLog.setMacAddressSelf(deviceLocationInfo.getMacAddressSelf());
        deviceLocationExceptionOpLog.setMacAddressNetwork(deviceLocationInfo.getMacAddressNetwork());
        deviceLocationExceptionOpLog.setLongitude(deviceLocationInfo.getLongitude());
        deviceLocationExceptionOpLog.setLatitude(deviceLocationInfo.getLatitude());

        deviceLocationExceptionOpLog.setOperateTime(new Date(deviceLocationUploadPo.getOperateTime()));
        deviceLocationExceptionOpLog.setCreateTime(new Date());
        deviceLocationExceptionOpLog.setOperateType(deviceLocationUploadPo.getOperateType());
        return deviceLocationExceptionOpLog;
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
        log.info("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationConsume param {}", JsonHelper.toJson(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> handleResult = this.uploadLocationInfo(deviceLocationUploadPo);
            if (!handleResult.isSuccess()) {
                log.warn("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationConsume uploadLocationInfo fail {} {}", JsonHelper.toJson(deviceLocationUploadPo), JsonHelper.toJson(handleResult));
                return result.toFail(handleResult.getMessage());
            }
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationConsume exception {}", JsonHelper.toJson(deviceLocationUploadPo), e);
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
        log.info("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationExceptionOpConsume param {}", JsonHelper.toJson(deviceLocationUploadPo));
        Result<Boolean> result = Result.success();
        try {
            // 上传异常记录
            final Result<Boolean> handleResult = this.uploadLocationExceptionOpInfo(deviceLocationUploadPo);
            if (!handleResult.isSuccess()) {
                log.warn("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationExceptionOpConsume handleDmsUploadDeviceLocationExceptionOpConsume fail {}", JsonHelper.toJson(deviceLocationUploadPo));
                return result.toFail(handleResult.getMessage());
            }

            // 发送异常提醒 封装MSP todo
        } catch (Exception e) {
            log.error("DeviceLocationServiceImpl.handleDmsUploadDeviceLocationExceptionOpConsume exception {}", JsonHelper.toJson(deviceLocationUploadPo), e);
            result.toFail("系统异常");
        }
        return result;
    }
}
