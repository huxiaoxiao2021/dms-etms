package com.jd.bluedragon.core.jsf.wlLbs.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.wlLbs.dto.fence.FenceData;
import com.jd.bluedragon.core.jsf.wlLbs.dto.fence.TransFenceInfoVo;
import com.jd.bluedragon.core.jsf.wlLbs.dto.fenceForWeb.Geometry;
import com.jd.bluedragon.core.jsf.wlLbs.dto.qo.QueryTransFenceBySiteIdQo;
import com.jd.bluedragon.core.jsf.wlLbs.manager.WlLbsApiManager;
import com.jd.bluedragon.core.jsf.wlLbs.manager.WlLbsApiWrapResultManager;
import com.jd.bluedragon.core.jsf.wlLbs.utils.GeoUtils;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.dms.java.utils.sdk.constants.ResultCodeConstant;
import com.jd.lbs.geocode.api.base.BaseResponse;
import com.jd.lbs.geocode.api.dto.GisPointDto;
import com.jd.lbs.jdlbsapi.dto.LocationRequestDto;
import com.jd.lbs.jdlbsapi.dto.LocationResultDto;
import com.jd.lbs.jdlbsapi.dto.drawtool.PointDto;
import com.jd.ql.basic.domain.BaseSite;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.gis.trans.fence.api.vo.req.query.QueryFenceReq;
import com.jdl.gis.trans.fence.api.vo.resp.query.QueryFenceResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 地理编码服务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-11 16:44:12 周五
 */
@Component
public class WlLbsApiWrapResultManagerImpl implements WlLbsApiWrapResultManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.wlLbs.WlLbsApiWrapResultManagerImpl.";

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WlLbsApiManager wlLbsApiManager;

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    /**
     * 地理编码服务：根据国标全地址（包含详细的省+市+区+县乡+详细地址），获取对应的经纬度位置坐标。
     * 例如：在门店到家服务中，根据用户填写的地址信息获得经纬度坐标，匹配所属商铺围栏，实现到家服务任务自动分派。
     *
     * @param appKey  appKey
     * @param address 国标全地址（省+市+区+县乡+详细地址）
     * @return ip地址结果
     * @author fanggang7
     * @time 2022-11-11 17:01:40 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiWrapResultManagerImpl.geo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<GisPointDto> geo(String appKey, String address) {
        Result<GisPointDto> result = Result.success();
        try {
            logInfo("jsfGeocodingService.geo param: {} , {}", appKey, address);
            final BaseResponse<GisPointDto> rpcResult = wlLbsApiManager.geo(appKey, address);
            logInfo("jsfGeocodingService.geo param: {} , {} result: {}", appKey, address, JsonHelper.toJson(rpcResult));
            if (rpcResult == null) {
                log.warn("jsfGeocodingService.geo result null: {} {}", appKey, address);
                return result.toFail("调用接口结果为空");
            }
            if (!Objects.equals(rpcResult.getStatus(), ResultCodeConstant.SUCCESS)) {
                log.warn("jsfGeocodingService.geo result fail: {} {}", appKey, address);
                return result.toFail(String.format("调用接口失败 %s", rpcResult.getMessage()));
            }
            result.setData(rpcResult.getResult());
        } catch (Exception e) {
            log.error("jsfGeocodingService.geo exception ", e);
            result.toFail("系统异常，调用地理编码服务异常！");
        }
        return result;
    }

    /**
     * 逆地理编码服务：根据经纬度坐标，获取对应的国标全地址。
     * 例如：用户通过手机 app 在地图商定位选址，可自动返回文本全地址，免用户输入。
     *
     * @param appKey    appKey
     * @param latitude  经度
     * @param longitude 纬度
     * @return 地理位置结果
     * @author fanggang7
     * @time 2022-11-11 17:03:49 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiWrapResultManagerImpl.regeo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<GisPointDto> regeo(String appKey, BigDecimal latitude, BigDecimal longitude) {
        Result<GisPointDto> result = Result.success();
        try {
            logInfo("jsfGeocodingService.regeo param: {} , {} , {}", appKey, latitude, longitude);
            final BaseResponse<GisPointDto> rpcResult = wlLbsApiManager.regeo(appKey, latitude, longitude);
            logInfo("jsfGeocodingService.regeo param: {} , {} , {} result {}", appKey, latitude, longitude, JsonHelper.toJson(rpcResult));
            if (rpcResult == null) {
                log.warn("jsfGeocodingService.regeo result null: {} , {} , {}", appKey, latitude, longitude);
                return result.toFail("调用接口结果为空");
            }
            if (!Objects.equals(rpcResult.getStatus(), ResultCodeConstant.SUCCESS)) {
                log.warn("jsfGeocodingService.regeo result fail: {} , {} , {}", appKey, latitude, longitude);
                return result.toFail(String.format("调用接口失败 %s", rpcResult.getMessage()));
            }
            result.setData(rpcResult.getResult());
        } catch (Exception e) {
            log.error("jsfGeocodingService.regeo exception ", e);
            result.toFail("系统异常，调用地理编码服务异常！");
        }
        return result;
    }

    /**
     * 根据网点编码查询围栏信息
     *
     * @param appKey        appKey
     * @param queryFenceReq 请求参数
     * @return 围栏信息
     * @author fanggang7
     * @time 2022-11-11 17:07:22 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiWrapResultManagerImpl.queryTransFenceByCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<QueryFenceResp> queryTransFenceByCode(String appKey, QueryFenceReq queryFenceReq) {
        Result<QueryFenceResp> result = Result.success();
        try {
            logInfo("jsfTransFenceQueryService.queryTransFenceByCode param: {} , {}", appKey, JsonHelper.toJson(queryFenceReq));
            final com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> rpcResult = wlLbsApiManager.queryTransFenceByCode(appKey, queryFenceReq);
            logInfo("jsfTransFenceQueryService.queryTransFenceByCode param: {} , {} result {}", appKey, JsonHelper.toJson(queryFenceReq), JsonHelper.toJson(rpcResult));
            if (rpcResult == null) {
                log.warn("jsfGeocodingService.queryTransFenceByCode result null: {} {}", appKey, JsonHelper.toJson(queryFenceReq));
                return result.toFail("调用接口结果为空");
            }
            if (!Objects.equals(rpcResult.getStatusCode(), ResultCodeConstant.SUCCESS)) {
                log.warn("jsfGeocodingService.queryTransFenceByCode result fail: {} {}", appKey, JsonHelper.toJson(queryFenceReq));
                return result.toFail(String.format("调用接口失败 %s", rpcResult.getMessage()));
            }
            result.setData(rpcResult.getResult());
        } catch (Exception e) {
            log.error("jsfGeocodingService.queryTransFenceByCode exception ", e);
            result.toFail("系统异常，调用围栏查询服务异常！");
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiWrapResultManagerImpl.queryTransFenceBySiteId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<QueryFenceResp> queryTransFenceBySiteId(Integer siteCode) {
        Result<QueryFenceResp> result = Result.success();
        try {
            logInfo("queryTransFenceBySiteId param: {}", siteCode);
            final BaseSite siteInfo = baseMajorManager.getSiteBySiteCode(siteCode);
            if (siteInfo == null) {
                log.warn("queryTransFenceBySiteId getSiteBySiteCode null: {}", siteCode);
                return result;
            }

            final QueryFenceReq queryFenceReq = new QueryFenceReq();
            queryFenceReq.setNodeCode(siteInfo.getDmsCode());
            final com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> rpcResult = wlLbsApiManager.queryTransFenceByCode(null, queryFenceReq);
            logInfo("queryTransFenceByCodeForWeb wlLbsApiManager.queryTransFenceByCode param: {} result {}", JsonHelper.toJson(queryFenceReq), JsonHelper.toJson(rpcResult));
            if (rpcResult == null) {
                log.warn("jsfGeocodingService.queryTransFenceByCode result null: {}", siteCode);
                return result.toFail("调用接口结果为空");
            }
            if (!Objects.equals(rpcResult.getStatusCode(), ResultCodeConstant.SUCCESS)) {
                log.warn("queryTransFenceByCodeForWeb wlLbsApiManager.queryTransFenceByCode result fail: {}", JsonHelper.toJson(queryFenceReq));
                return result.toFail(String.format("调用接口失败 %s", rpcResult.getMessage()));
            }
            result.setData(rpcResult.getResult());
        } catch (Exception e) {
            log.error("queryTransFenceByCodeForWeb exception ", e);
            result.toFail("系统异常，调用围栏查询服务异常！");
        }
        return result;
    }

    /**
     * 根据网点编码查询围栏信息
     *
     * @param appKey        appKey
     * @param queryFenceReq 请求参数
     * @return 围栏信息
     * @author fanggang7
     * @time 2022-11-11 17:07:22 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiWrapResultManagerImpl.queryTransFenceByCodeForWeb", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<FenceData> queryTransFenceByCodeForWeb(String appKey, QueryFenceReq queryFenceReq) {
        Result<FenceData> result = Result.success();
        try {
            logInfo("queryTransFenceByCodeForWeb wlLbsApiManager.queryTransFenceByCode param: {} , {}", appKey, JsonHelper.toJson(queryFenceReq));
            final com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> rpcResult = wlLbsApiManager.queryTransFenceByCode(appKey, queryFenceReq);
            logInfo("queryTransFenceByCodeForWeb wlLbsApiManager.queryTransFenceByCode param: {} , {} result {}", appKey, JsonHelper.toJson(queryFenceReq), JsonHelper.toJson(rpcResult));
            if (rpcResult == null) {
                log.warn("queryTransFenceByCodeForWeb wlLbsApiManager.queryTransFenceByCode result null: {} {}", appKey, JsonHelper.toJson(queryFenceReq));
                return result.toFail("调用接口结果为空");
            }
            if (!Objects.equals(rpcResult.getStatusCode(), ResultCodeConstant.SUCCESS)) {
                log.warn("jsfGeocodingService.queryTransFenceByCode result fail: {} {}", appKey, JsonHelper.toJson(queryFenceReq));
                return result.toFail(String.format("调用接口失败 %s", rpcResult.getMessage()));
            }
            QueryFenceResp queryFenceResp = rpcResult.getResult();
            if (queryFenceResp == null) {
                log.warn("queryTransFenceByCodeForWeb wlLbsApiManager.queryTransFenceByCode result empty: {}", JsonHelper.toJson(queryFenceReq));
                return result;
            }
            final FenceData fenceData = new FenceData();
            List<TransFenceInfoVo> transFenceInfoVoList = new ArrayList<>();
            fenceData.setTransFenceInfoVoList(transFenceInfoVoList);
            fenceData.setCommonFenceGeoJson(queryFenceResp.getCommonFenceGeoJson());
            final List<com.jdl.gis.trans.fence.api.vo.resp.query.TransFenceInfoVo> transFenceInfoVoListData = queryFenceResp.getTransFenceInfoVoList();
            for (com.jdl.gis.trans.fence.api.vo.resp.query.TransFenceInfoVo transFenceInfoVoItem : transFenceInfoVoListData) {
                final TransFenceInfoVo transFenceInfoVoData = new TransFenceInfoVo();
                BeanUtils.copyProperties(transFenceInfoVoItem, transFenceInfoVoData);
                final JSONObject jsonObject = GeoUtils.wktToJson(transFenceInfoVoItem.getFenceWkt());
                transFenceInfoVoData.setGeometry(JSON.parseObject(JSON.toJSONString(jsonObject), Geometry.class));
                transFenceInfoVoList.add(transFenceInfoVoData);
            }
            result.setData(fenceData);
        } catch (Exception e) {
            log.error("queryTransFenceByCodeForWeb exception ", e);
            result.toFail("系统异常，调用围栏查询服务异常！");
        }
        return result;
    }

    /**
     * 根据场地ID查询围栏信息
     *
     * @param queryTransFenceBySiteIdQo 查询参数
     * @return 围栏信息
     * @author fanggang7
     * @time 2022-11-11 17:07:22 周五
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiWrapResultManagerImpl.queryTransFenceBySiteIdForWeb", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<FenceData> queryTransFenceBySiteIdForWeb(QueryTransFenceBySiteIdQo queryTransFenceBySiteIdQo) {
        Result<FenceData> result = Result.success();
        try {
            logInfo("jsfTransFenceQueryService.queryTransFenceBySiteId param: {}", JsonHelper.toJson(queryTransFenceBySiteIdQo));
            final BaseSite siteInfo = baseMajorManager.getSiteBySiteCode(queryTransFenceBySiteIdQo.getSiteId());
            if (siteInfo == null) {
                log.warn("jsfTransFenceQueryService.queryTransFenceBySiteId getSiteBySiteCode null: {}", JsonHelper.toJson(queryTransFenceBySiteIdQo));
                return result;
            }

            final QueryFenceReq queryFenceReq = new QueryFenceReq();
            queryFenceReq.setNodeCode(siteInfo.getDmsCode());
            final com.jdl.gis.trans.fence.api.vo.resp.base.BaseResponse<QueryFenceResp> rpcResult = wlLbsApiManager.queryTransFenceByCode(null, queryFenceReq);
            logInfo("jsfTransFenceQueryService.queryTransFenceByCode param: {} result {}", JsonHelper.toJson(queryFenceReq), JsonHelper.toJson(rpcResult));
            if (rpcResult == null) {
                log.warn("jsfGeocodingService.queryTransFenceByCode result null: {}", JsonHelper.toJson(queryTransFenceBySiteIdQo));
                return result.toFail("调用接口结果为空");
            }
            if (!Objects.equals(rpcResult.getStatusCode(), ResultCodeConstant.SUCCESS)) {
                log.warn("jsfGeocodingService.queryTransFenceByCode result fail: {}", JsonHelper.toJson(queryFenceReq));
                return result.toFail(String.format("调用接口失败 %s", rpcResult.getMessage()));
            }
            QueryFenceResp queryFenceResp = rpcResult.getResult();
            if (queryFenceResp == null) {
                log.warn("jsfGeocodingService.queryTransFenceByCode result empty: {}", JsonHelper.toJson(queryFenceReq));
                return result;
            }
            final FenceData fenceData = new FenceData();
            List<TransFenceInfoVo> transFenceInfoVoList = new ArrayList<>();
            fenceData.setTransFenceInfoVoList(transFenceInfoVoList);
            fenceData.setCommonFenceGeoJson(queryFenceResp.getCommonFenceGeoJson());
            final List<com.jdl.gis.trans.fence.api.vo.resp.query.TransFenceInfoVo> transFenceInfoVoListData = queryFenceResp.getTransFenceInfoVoList();
            for (com.jdl.gis.trans.fence.api.vo.resp.query.TransFenceInfoVo transFenceInfoVoItem : transFenceInfoVoListData) {
                final TransFenceInfoVo transFenceInfoVoData = new TransFenceInfoVo();
                BeanUtils.copyProperties(transFenceInfoVoItem, transFenceInfoVoData);
                final JSONObject jsonObject = GeoUtils.wktToJson(transFenceInfoVoItem.getFenceWkt());
                transFenceInfoVoData.setGeometry(JSON.parseObject(JSON.toJSONString(jsonObject), Geometry.class));
                transFenceInfoVoList.add(transFenceInfoVoData);
            }
            result.setData(fenceData);
        } catch (Exception e) {
            log.error("jsfGeocodingService.queryTransFenceByCode exception ", e);
            result.toFail("系统异常，调用围栏查询服务异常！");
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiWrapResultManagerImpl.getLocationByIp", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<LocationResultDto> getLocationByIp(String appKey, LocationRequestDto locationRequestDto) {
        Result<LocationResultDto> result = Result.success();
        try {
            logInfo("jsfLocationService.getLocationByIp param: {} , {}", appKey, JsonHelper.toJson(locationRequestDto));
            final com.jd.lbs.jdlbsapi.dto.BaseResponse<LocationResultDto> rpcResult = wlLbsApiManager.getLocationByIp(appKey, locationRequestDto);
            logInfo("jsfLocationService.getLocationByIp result: {} , {} , {}", appKey, locationRequestDto.getIp(), JsonHelper.toJson(rpcResult));
            if (rpcResult == null) {
                log.warn("jsfGeocodingService.getLocationByIp result null: {} , {}", appKey, JsonHelper.toJson(locationRequestDto));
                return result.toFail("调用接口结果为空");
            }
            if (!Objects.equals(rpcResult.getStatus(), ResultCodeConstant.SUCCESS)) {
                log.warn("jsfGeocodingService.getLocationByIp result fail: {} , {}", appKey, JsonHelper.toJson(locationRequestDto));
                return result.toFail(String.format("调用接口失败 %s", rpcResult.getMessage()));
            }
            result.setData(rpcResult.getResult());
        } catch (Exception e) {
            log.error("jsfGeocodingService.geo exception ", e);
            result.toFail("系统异常，调用围栏查询服务异常！");
        }
        return result;
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WlLbsApiWrapResultManagerImpl.getLength", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<BigDecimal> getLength(String appKey, PointDto startPoint, PointDto endPoint) {
        Result<BigDecimal> result = Result.success();
        try {
            logInfo("jsfGeoToolService.getLength param: {} , {} , {}", appKey, JsonHelper.toJson(startPoint), JsonHelper.toJson(endPoint));
            final com.jd.lbs.jdlbsapi.dto.BaseResponse<BigDecimal> rpcResult = wlLbsApiManager.getLength(appKey, startPoint, endPoint);
            logInfo("jsfGeoToolService.getLength result: {} , {} , {} , {}", appKey, JsonHelper.toJson(startPoint), JsonHelper.toJson(endPoint), JsonHelper.toJson(rpcResult));
            if (rpcResult == null) {
                log.warn("jsfGeoToolService.getLength result null: {} , {} , {}", appKey, JsonHelper.toJson(startPoint), JsonHelper.toJson(endPoint));
                return result.toFail("调用接口结果为空");
            }
            if (!Objects.equals(rpcResult.getStatus(), ResultCodeConstant.SUCCESS)) {
                log.warn("jsfGeoToolService.getLength result fail: {} , {} , {}", appKey, JsonHelper.toJson(startPoint), JsonHelper.toJson(endPoint));
                return result.toFail(String.format("调用接口失败 %s", rpcResult.getMessage()));
            }
            result.setData(rpcResult.getResult());
        } catch (Exception e) {
            log.error("jsfGeoToolService.getLength exception ", e);
            result.toFail("系统异常，调用围栏查询服务异常！");
        }
        return result;
    }
}
