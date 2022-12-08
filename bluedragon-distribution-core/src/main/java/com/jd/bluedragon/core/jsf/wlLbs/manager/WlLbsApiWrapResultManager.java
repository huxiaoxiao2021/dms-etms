package com.jd.bluedragon.core.jsf.wlLbs.manager;

import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.lbs.geocode.api.dto.GisPointDto;
import com.jd.lbs.jdlbsapi.dto.LocationRequestDto;
import com.jd.lbs.jdlbsapi.dto.LocationResultDto;
import com.jd.lbs.jdlbsapi.dto.drawtool.PointDto;
import com.jdl.gis.trans.fence.api.vo.req.query.QueryFenceReq;
import com.jdl.gis.trans.fence.api.vo.resp.query.QueryFenceResp;

import java.math.BigDecimal;

/**
 * 物流地理编码服务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-23 14:19:01 周三
 */
public interface WlLbsApiWrapResultManager {

    /**
     * 地理编码服务：根据国标全地址（包含详细的省+市+区+县乡+详细地址），获取对应的经纬度位置坐标。
     * 例如：在门店到家服务中，根据用户填写的地址信息获得经纬度坐标，匹配所属商铺围栏，实现到家服务任务自动分派。
     * @param appId appKey
     * @param address 国标全地址（省+市+区+县乡+详细地址）
     * @return ip地址结果
     * @author fanggang7
     * @time 2022-11-11 17:01:40 周五
     */
    Result<GisPointDto> geo(String appId, String address);

    /**
     * 逆地理编码服务：根据经纬度坐标，获取对应的国标全地址。
     * 例如：用户通过手机 app 在地图商定位选址，可自动返回文本全地址，免用户输入。
     * @param appId appKey
     * @param latitude 经度
     * @param longitude 纬度
     * @return 地理位置结果
     * @author fanggang7
     * @time 2022-11-11 17:03:49 周五
     */
    Result<GisPointDto> regeo(String appId, BigDecimal latitude, BigDecimal longitude);

    /**
     * 根据网点编码查询围栏信息
     * @param appKey appKey
     * @param queryFenceReq 请求参数
     * @return 围栏信息
     * @author fanggang7
     * @time 2022-11-11 17:07:22 周五
     */
    Result<QueryFenceResp> queryTransFenceByCode(String appKey, QueryFenceReq queryFenceReq);

    /**
     * 根据IP获取经纬度
     * @param appKey appKey
     * @param locationRequestDto IP请求
     * @return 经纬度结果
     * @author fanggang7
     * @time 2022-11-11 17:43:26 周五
     */
    Result<LocationResultDto> getLocationByIp(String appKey, LocationRequestDto locationRequestDto);

    /**
     * 计算两点间的直线距离
     * @param appKey appKey
     * @param startPoint 起点
     * @param endPoint 终点
     * @return 距离信息
     * @author fanggang7
     * @time 2022-11-11 17:46:19 周五
     */
    Result<BigDecimal> getLength(String appKey, PointDto startPoint, PointDto endPoint);
}
