package com.jd.bluedragon.core.jsf.wlLbs.manager.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.wlLbs.manager.WlLbsApiManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.lbs.geocode.api.base.BaseResponse;
import com.jd.lbs.geocode.api.dto.GisPointDto;
import com.jd.lbs.jdlbsapi.dto.LocationRequestDto;
import com.jd.lbs.jdlbsapi.dto.LocationResultDto;
import com.jd.lbs.jdlbsapi.dto.drawtool.PointDto;
import com.jd.ql.basic.domain.BaseSite;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.gis.trans.fence.api.vo.req.query.QueryFenceReq;
import com.jdl.gis.trans.fence.api.vo.resp.query.QueryFenceResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 地理编码服务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-11 16:44:12 周五
 */
@Component
public class WlLbsApiManagerImpl implements WlLbsApiManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.wlLbs.WlLbsApiManagerImpl.";

    // 地址、经纬度解析服务
    @Resource
    @Qualifier("jsfGeocodingService")
    private com.jd.lbs.geocode.api.GeocodingService jsfGeocodingService;
    @Value("${jsf.wlLbs.geoCoding.appKey}")
    private String appKey_jsfGeocodingService;

    // 计算两点间的直线距离
    @Resource
    @Qualifier("jsfGeoToolService")
    com.jd.lbs.jdlbsapi.geotool.GeoToolService jsfGeoToolService;
    @Value("${jsf.wlLbs.geoTool.appKey}")
    private String appKey_jsfGeoToolService;

    // 根据IP获取经纬度及地址信息
    @Resource
    @Qualifier("jsfLocationService")
    com.jd.lbs.jdlbsapi.geotool.LocationService jsfLocationService;
    @Value("${jsf.wlLbs.locationService.appKey}")
    private String appKey_jsfLocationService;

    // 围栏信息查询
    @Resource
    @Qualifier("jsfTransFenceQueryService")
    private com.jdl.gis.trans.fence.api.service.TransFenceQueryService jsfTransFenceQueryService;
    @Value("${jsf.wlLbs.transFenceQuery.appKey}")
    private String appKey_transFenceQuery;
    @Value("${jsf.wlLbs.transFenceQuery.nodeDateSource}")
    private String transFenceQueryNodeDateSource;

    @Autowired
    private BaseMajorManager baseMajorManager;

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }


    /**
     * 地理编码服务：根据国标全地址（包含详细的省+市+区+县乡+详细地址），获取对应的经纬度位置坐标。
     * 例如：在门店到家服务中，根据用户填写的地址信息获得经纬度坐标，匹配所属商铺围栏，实现到家服务任务自动分派。
     *
     * @param appKey   appKey
     * @param address 国标全地址（省+市+区+县乡+详细地址）
     * @return ip地址结果
     * @author fanggang7
     * @time 2022-11-11 17:01:40 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiManagerImpl.geo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public BaseResponse<GisPointDto> geo(String appKey, String address) {
        BaseResponse<GisPointDto> result = new BaseResponse<>();
        try {
            logInfo("jsfGeocodingService.geo param: {} , {}", appKey, address);
            if (appKey == null) {
                appKey = appKey_jsfGeocodingService;
            }
            result = jsfGeocodingService.geo(appKey, address);
            logInfo("jsfGeocodingService.geo param: {} , {} result: {}", appKey, address, JsonHelper.toJson(result));
            if(result == null){
                log.warn("jsfGeocodingService.geo result null: {} {}", appKey, address);
                return null;
            }
        } catch (Exception e) {
            log.error("jsfGeocodingService.geo exception ",e);
            if (result == null) {
                result = new BaseResponse<>();
            }
            result.setStatus(-1);
            result.setMessage("系统异常，调用地理编码服务异常！");
        }
        return result;
    }

    /**
     * 逆地理编码服务：根据经纬度坐标，获取对应的国标全地址。
     * 例如：用户通过手机 app 在地图商定位选址，可自动返回文本全地址，免用户输入。
     *
     * @param appKey     appKey
     * @param latitude  经度
     * @param longitude 纬度
     * @return 地理位置结果
     * @author fanggang7
     * @time 2022-11-11 17:03:49 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiManagerImpl.regeo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public BaseResponse<GisPointDto> regeo(String appKey, BigDecimal latitude, BigDecimal longitude) {
        BaseResponse<GisPointDto> result = new BaseResponse<>();
        try {
            logInfo("jsfGeocodingService.regeo param: {} , {} , {}", appKey, latitude, longitude);
            if (appKey == null) {
                appKey = appKey_jsfGeocodingService;
            }
            result = jsfGeocodingService.regeo(appKey, latitude, longitude);
            logInfo("jsfGeocodingService.regeo param: {} , {} , {} result {}", appKey, latitude, longitude, JsonHelper.toJson(result));
            if(result == null){
                log.warn("jsfGeocodingService.regeo result null: {} {} {}", appKey, latitude, longitude);
                return null;
            }
        } catch (Exception e) {
            log.error("jsfGeocodingService.regeo exception ",e);
            if (result == null) {
                result = new BaseResponse<>();
            }
            result.setStatus(-1);
            result.setMessage("系统异常，调用地理编码服务异常！");
        }
        return result;
    }

    /**
     * 根据网点编码查询围栏信息
     *
     * @param appKey appKey
     * @param queryFenceReq 请求参数
     * @return 围栏信息
     * @author fanggang7
     * @time 2022-11-11 17:07:22 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiManagerImpl.queryTransFenceByCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> queryTransFenceByCode(String appKey, QueryFenceReq queryFenceReq) {
        com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> result = new com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<>();
        try {
            logInfo("jsfTransFenceQueryService.queryTransFenceByCode param: {} , {}", appKey, JsonHelper.toJson(queryFenceReq));
            if (appKey == null) {
                appKey = appKey_transFenceQuery;
            }
            // 默认的nodeDateSource
            if(queryFenceReq.getNodeDataSource() == null){
                queryFenceReq.setNodeDataSource(transFenceQueryNodeDateSource);
            }
            result = jsfTransFenceQueryService.queryTransFenceByCode(appKey, queryFenceReq);
            logInfo("jsfTransFenceQueryService.queryTransFenceByCode param: {} , {} result {}", appKey, JsonHelper.toJson(queryFenceReq), JsonHelper.toJson(result));
            if(result == null){
                log.warn("jsfTransFenceQueryService.queryTransFenceByCode result null: {} {}", appKey, JsonHelper.toJson(queryFenceReq));
                return result;
            }
            result = new com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<>();
            result.setStatusCode(200);
            result.setResult(JSON.parseObject("{\n" +
                    "        \"transFenceInfoVoList\": [\n" +
                    "            {\n" +
                    "                \"lng\": \"116.54655257242666\",\n" +
                    "                \"fenceType\": \"2\",\n" +
                    "                \"areaShape\": \"2\",\n" +
                    "                \"nodeCode\": \"010F013\",\n" +
                    "                \"updateUserName\": \"bjyanzy\",\n" +
                    "                \"effectiveEndTime\": \"2023-06-06 00:00:00\",\n" +
                    "                \"updateTime\": \"2022-11-27 11:42:26\",\n" +
                    "                \"fenceWkt\": \"POLYGON ((116.54387 39.725335, 116.544492 39.725335, 116.544203 39.721341, 116.549685 39.721225, 116.549513 39.719236, 116.548408 39.719257, 116.548188 39.719509, 116.544085 39.719587, 116.544074 39.719806, 116.543548 39.719814, 116.54387 39.725335))\",\n" +
                    "                \"updateUserCode\": \"bjyanzy\",\n" +
                    "                \"yn\": 0,\n" +
                    "                \"class\": \"com.jdl.gis.trans.fence.api.vo.resp.query.TransFenceInfoVo\",\n" +
                    "                \"lat\": \"39.71965034152937\",\n" +
                    "                \"effectiveBeginTime\": \"2022-11-27 11:40:00\"\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"showBtnFlag\": false,\n" +
                    "        \"class\": \"com.jdl.gis.trans.fence.api.vo.resp.query.QueryFenceResp\",\n" +
                    "        \"commonFenceGeoJson\": []\n" +
                    "    }", QueryFenceResp.class));
        } catch (Exception e) {
            log.error("jsfTransFenceQueryService.queryTransFenceByCode exception ",e);
            if (result == null) {
                result = new com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<>();
            }
            result.setStatusCode(-1);
            result.setMessage("系统异常，调用围栏查询服务异常！");
        }
        return result;
    }

    /**
     * 根据场地ID查询围栏信息
     *
     * @param siteCode 场地ID
     * @return 围栏信息
     * @author fanggang7
     * @time 2022-11-11 17:07:22 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiManagerImpl.queryTransFenceBySiteId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> queryTransFenceBySiteId(Integer siteCode) {
        com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> result = new com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<>();
        try {
            logInfo("jsfTransFenceQueryService.queryTransFenceBySiteId param: {}", siteCode);
            final BaseSite siteInfo = baseMajorManager.getSiteBySiteCode(siteCode);
            if (siteInfo == null) {
                log.warn("jsfTransFenceQueryService.queryTransFenceBySiteId getSiteBySiteCode null: {}", siteCode);
                return result;
            }

            String appKey = appKey_transFenceQuery;
            final QueryFenceReq queryFenceReq = new QueryFenceReq();
            // 默认的nodeDateSource
            if(queryFenceReq.getNodeDataSource() == null){
                queryFenceReq.setNodeDataSource(transFenceQueryNodeDateSource);
            }
            result = jsfTransFenceQueryService.queryTransFenceByCode(appKey, queryFenceReq);
            logInfo("jsfTransFenceQueryService.queryTransFenceBySiteId param: {} , {} result {}", appKey, JsonHelper.toJson(queryFenceReq), JsonHelper.toJson(result));
            if(result == null){
                log.warn("jsfTransFenceQueryService.queryTransFenceByCode result null: {} {}", appKey, JsonHelper.toJson(queryFenceReq));
                return result;
            }
            result = new com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<>();
            result.setStatusCode(200);
            result.setResult(JSON.parseObject("{\n" +
                    "        \"transFenceInfoVoList\": [\n" +
                    "            {\n" +
                    "                \"lng\": \"116.54655257242666\",\n" +
                    "                \"fenceType\": \"2\",\n" +
                    "                \"areaShape\": \"2\",\n" +
                    "                \"nodeCode\": \"010F013\",\n" +
                    "                \"updateUserName\": \"bjyanzy\",\n" +
                    "                \"effectiveEndTime\": \"2023-06-06 00:00:00\",\n" +
                    "                \"updateTime\": \"2022-11-27 11:42:26\",\n" +
                    "                \"fenceWkt\": \"POLYGON ((116.54387 39.725335, 116.544492 39.725335, 116.544203 39.721341, 116.549685 39.721225, 116.549513 39.719236, 116.548408 39.719257, 116.548188 39.719509, 116.544085 39.719587, 116.544074 39.719806, 116.543548 39.719814, 116.54387 39.725335))\",\n" +
                    "                \"updateUserCode\": \"bjyanzy\",\n" +
                    "                \"yn\": 0,\n" +
                    "                \"class\": \"com.jdl.gis.trans.fence.api.vo.resp.query.TransFenceInfoVo\",\n" +
                    "                \"lat\": \"39.71965034152937\",\n" +
                    "                \"effectiveBeginTime\": \"2022-11-27 11:40:00\"\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"showBtnFlag\": false,\n" +
                    "        \"class\": \"com.jdl.gis.trans.fence.api.vo.resp.query.QueryFenceResp\",\n" +
                    "        \"commonFenceGeoJson\": []\n" +
                    "    }", QueryFenceResp.class));
        } catch (Exception e) {
            log.error("jsfTransFenceQueryService.queryTransFenceBySiteId exception ",e);
            if (result == null) {
                result = new com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<>();
            }
            result.setStatusCode(-1);
            result.setMessage("系统异常，调用围栏查询服务异常！");
        }
        return result;
    }

    /**
     * 根据IP获取经纬度及地址信息
     *
     * @param appKey             appKey
     * @param locationRequestDto IP请求
     * @return 经纬度结果
     * @author fanggang7
     * @time 2022-11-11 17:43:26 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiManagerImpl.getLocationByIp", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public com.jd.lbs.jdlbsapi.dto.BaseResponse<LocationResultDto> getLocationByIp(String appKey, LocationRequestDto locationRequestDto) {
        com.jd.lbs.jdlbsapi.dto.BaseResponse<LocationResultDto> result = new com.jd.lbs.jdlbsapi.dto.BaseResponse<>();
        try {
            logInfo("jsfLocationService.getLocationByIp param: {} , {}", appKey, JsonHelper.toJson(locationRequestDto));
            if (appKey == null) {
                appKey = appKey_jsfLocationService;
            }
            result = jsfLocationService.getLocationByIp(appKey, locationRequestDto);
            logInfo("jsfLocationService.getLocationByIp result: {} , {} , {}", appKey, locationRequestDto.getIp(), JsonHelper.toJson(result));
            if(result == null){
                log.warn("jsfLocationService.queryTransFenceByCode result null: {} {}", appKey, JsonHelper.toJson(locationRequestDto));
                return null;
            }
        } catch (Exception e) {
            log.error("jsfLocationService.getLocationByIp exception ",e);
            if (result == null) {
                result = new com.jd.lbs.jdlbsapi.dto.BaseResponse<>();
                result.setStatus(-1);
            }
            result.setMessage("系统异常，IP获取经纬度服务异常！");
        }
        return null;
    }

    /**
     * 计算两点间的直线距离
     *
     * @param appKey     appKey
     * @param startPoint 起点
     * @param endPoint   终点
     * @return 距离信息
     * @author fanggang7
     * @time 2022-11-11 17:46:19 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiManagerImpl.getLength", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public com.jd.lbs.jdlbsapi.dto.BaseResponse<BigDecimal> getLength(String appKey, PointDto startPoint, PointDto endPoint) {
        com.jd.lbs.jdlbsapi.dto.BaseResponse<BigDecimal> result = new com.jd.lbs.jdlbsapi.dto.BaseResponse<>();
        try {
            logInfo("jsfGeoToolService.getLength param: {} , {} , {}", appKey, JsonHelper.toJson(startPoint), JsonHelper.toJson(endPoint));
            if (appKey == null) {
                appKey = appKey_jsfGeoToolService;
            }
            result = jsfGeoToolService.getLength(appKey, startPoint, endPoint);
            logInfo("jsfGeoToolService.getLength result: {} , {} , {} , {}", appKey, JsonHelper.toJson(startPoint), JsonHelper.toJson(endPoint), JsonHelper.toJson(result));
            if(result == null){
                log.warn("jsfGeoToolService.getLength result null: {} {} {}", appKey, JsonHelper.toJson(startPoint), JsonHelper.toJson(endPoint));
                return null;
            }
        } catch (Exception e) {
            log.error("jsfGeoToolService.getLength exception ",e);
            if (result == null) {
                result = new com.jd.lbs.jdlbsapi.dto.BaseResponse<>();
            }
            result.setStatus(-1);
            result.setMessage("系统异常，计算两点间的直线距离服务异常！");
        }
        return result;
    }
}
