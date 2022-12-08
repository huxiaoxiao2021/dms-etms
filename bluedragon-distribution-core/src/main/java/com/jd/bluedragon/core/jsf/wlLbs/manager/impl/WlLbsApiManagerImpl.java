package com.jd.bluedragon.core.jsf.wlLbs.manager.impl;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.jsf.wlLbs.manager.WlLbsApiManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.lbs.geocode.api.base.BaseResponse;
import com.jd.lbs.geocode.api.dto.GisPointDto;
import com.jd.lbs.jdlbsapi.dto.LocationRequestDto;
import com.jd.lbs.jdlbsapi.dto.LocationResultDto;
import com.jd.lbs.jdlbsapi.dto.drawtool.PointDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.gis.trans.fence.api.vo.req.query.QueryFenceReq;
import com.jdl.gis.trans.fence.api.vo.resp.query.QueryFenceResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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

    // 计算两点间的直线距离
    @Resource
    @Qualifier("jsfGeoToolService")
    com.jd.lbs.jdlbsapi.geotool.GeoToolService jsfGeoToolService;

    // 根据IP获取经纬度及地址信息
    @Resource
    @Qualifier("jsfLocationService")
    com.jd.lbs.jdlbsapi.geotool.LocationService jsfLocationService;

    // 围栏信息查询
    @Resource
    @Qualifier("jsfTransFenceQueryService")
    private com.jdl.gis.trans.fence.api.service.TransFenceQueryService jsfTransFenceQueryService;

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
    public com.jd.lbs.geocode.api.base.BaseResponse<GisPointDto> geo(String appKey, String address) {
        CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "jsfGeocodingService.geo");
        com.jd.lbs.geocode.api.base.BaseResponse<GisPointDto> result = new com.jd.lbs.geocode.api.base.BaseResponse<>();
        try {
            logInfo("jsfGeocodingService.geo param: {} , {}", appKey, address);
            result = jsfGeocodingService.geo(appKey, address);
            logInfo("jsfGeocodingService.geo param: {} , {} result: %{}", appKey, address, JsonHelper.toJson(result));
            if(result == null){
                log.warn("jsfGeocodingService.geo result null: {} {}", appKey, address);
                return null;
            }
        } catch (Exception e) {
            log.error("jsfGeocodingService.geo exception ",e);
            if (result == null) {
                result = new com.jd.lbs.geocode.api.base.BaseResponse<>();
            }
            result.setStatus(-1);
            result.setMessage("系统异常，调用地理编码服务异常！");
            Profiler.functionError(callerInfo);
        }finally{
            Profiler.registerInfoEnd(callerInfo);
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
    public com.jd.lbs.geocode.api.base.BaseResponse<GisPointDto> regeo(String appKey, BigDecimal latitude, BigDecimal longitude) {
        CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "jsfGeocodingService.regeo");
        BaseResponse<GisPointDto> result = new BaseResponse<>();
        try {
            logInfo("jsfGeocodingService.regeo param: {} , {} , {}", appKey, latitude, longitude);
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
            Profiler.functionError(callerInfo);
        }finally{
            Profiler.registerInfoEnd(callerInfo);
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
    public com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> queryTransFenceByCode(String appKey, QueryFenceReq queryFenceReq) {
        CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "jsfTransFenceQueryService.queryTransFenceByCode");
        com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> result = new com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<>();
        try {
            logInfo("jsfTransFenceQueryService.queryTransFenceByCode param: {} , {}", appKey, JsonHelper.toJson(queryFenceReq));
            result = jsfTransFenceQueryService.queryTransFenceByCode(appKey, queryFenceReq);
            logInfo("jsfTransFenceQueryService.queryTransFenceByCode param: {} , {} result {}", appKey, JsonHelper.toJson(queryFenceReq), JsonHelper.toJson(result));
            if(result == null){
                log.warn("jsfTransFenceQueryService.queryTransFenceByCode result null: {} {}", appKey, JsonHelper.toJson(queryFenceReq));
                return null;
            }
        } catch (Exception e) {
            log.error("jsfTransFenceQueryService.queryTransFenceByCode exception ",e);
            if (result == null) {
                result = new com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<>();
            }
            result.setStatusCode(-1);
            result.setMessage("系统异常，调用围栏查询服务异常！");
            Profiler.functionError(callerInfo);
        }finally{
            Profiler.registerInfoEnd(callerInfo);
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
    public com.jd.lbs.jdlbsapi.dto.BaseResponse<LocationResultDto> getLocationByIp(String appKey, LocationRequestDto locationRequestDto) {
        CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "jsfLocationService.getLocationByIp");
        com.jd.lbs.jdlbsapi.dto.BaseResponse<LocationResultDto> result = new com.jd.lbs.jdlbsapi.dto.BaseResponse<>();
        try {
            logInfo("jsfLocationService.getLocationByIp param: {} , {}", appKey, JsonHelper.toJson(locationRequestDto));
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
            Profiler.functionError(callerInfo);
        }finally{
            Profiler.registerInfoEnd(callerInfo);
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
    public com.jd.lbs.jdlbsapi.dto.BaseResponse<BigDecimal> getLength(String appKey, PointDto startPoint, PointDto endPoint) {
        CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "jsfGeoToolService.getLength");
        com.jd.lbs.jdlbsapi.dto.BaseResponse<BigDecimal> result = new com.jd.lbs.jdlbsapi.dto.BaseResponse<>();
        try {
            logInfo("jsfGeoToolService.getLength param: {} , {} , {}", appKey, JsonHelper.toJson(startPoint), JsonHelper.toJson(endPoint));
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
            Profiler.functionError(callerInfo);
        }finally{
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }
}
