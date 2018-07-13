package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.api.bnet.VrsBNetQueryAPI;
import com.jd.etms.api.bnet.req.BnetPerFormanceConfigJsfReq;
import com.jd.etms.api.common.dto.BaseDto;
import com.jd.etms.api.common.dto.CommonDto;
import com.jd.etms.api.common.dto.PageDto;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.etms.api.recommendroute.resp.RecommendRouteResp;
import com.jd.etms.api.transferwavemonitor.TransferWaveMonitorAPI;
import com.jd.etms.api.transferwavemonitor.req.TransferWaveMonitorReq;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorDetailResp;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorResp;
import com.jd.etms.api.waybill.VrsWaybillQueryAPI;
import com.jd.etms.sdk.compute.RouteComputeUtil;
import com.jd.etms.sdk.util.PerformanceTimeUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 路由系统的jsf接口，查询路由信息
 *
 * @author tangchunqing
 * @ClassName: VrsRouteTransferRelationManagerImpl
 * @Description: 路由系统接口
 * @date 2018年04月09日 14时:58分
 */
@Service("vrsRouteTransferRelationManager")
public class VrsRouteTransferRelationManagerImpl implements VrsRouteTransferRelationManager {
    private static final Logger logger = Logger.getLogger(VrsRouteTransferRelationManagerImpl.class);

    private static final Integer ROUTE_INTER_NODE_TYPE_CHENG_SHI_BIAN_MA = 0;

    private static final Integer ROUTE_INTER_NODE_TYPE_WANG_DIAN_BIAN_MA = 1;

    @Autowired
    private RouteComputeUtil routeComputeUtil;

    @Autowired
    private VrsBNetQueryAPI vrsBNetQueryApi;

    @Autowired
    private TransferWaveMonitorAPI transferWaveMonitorAPI;

    @Autowired
    private VrsWaybillQueryAPI vrsWaybillQueryAPI;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Value("${jsf.router.token}")
    private String vrsRouteTransferRelationApiToken;

    @Override
    @JProfiler(jKey = "DMS.BASE.VrsRouteTransferRelationManagerImpl.queryRecommendRoute", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String queryRecommendRoute(String startNode, String endNodeCode, Date predictSendTime, RouteProductEnum routeProduct) {
        try {
            CommonDto<RecommendRouteResp> commonDto = routeComputeUtil.queryRecommendRoute(vrsRouteTransferRelationApiToken, startNode, endNodeCode, predictSendTime, routeProduct);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null || StringHelper.isEmpty(commonDto.getData().getRecommendRouting())) {
                logger.warn("查询远程路由中转信息失败,参数列表：startNode:" + startNode + ",endNodeCode:" + endNodeCode + ",predictSendTime:" + predictSendTime.getTime() + ",routeProduct:" + routeProduct);
                logger.warn("查询远程路由中转信息失败，返回消息：" + commonDto.getMessage());
                return null;
            } else {
                logger.warn("查询远程路由成功：" + commonDto.getData().getRecommendRouting() + ",参数：startNode:" + startNode + ",endNodeCode:" + endNodeCode + ",predictSendTime:" + predictSendTime.getTime() + ",routeProduct:" + routeProduct);
                return commonDto.getData().getRecommendRouting();
            }
        } catch (Exception e) {
            logger.error("查询远程路由中转信息失败：" + e);
            return null;
        }

    }

    /**
     * 查询B网应履约时效
     *
     * @param configType    配置类型
     * @param bizzType      业务类型
     * @param startSiteNode 始发[城市/网点]编码
     * @param toSiteNode    目的[城市/网点]编码
     * @param pickUpEndTime 预约揽收截止时间
     * @return 应履约时效
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.VrsRouteTransferRelationManagerImpl.queryRoutePredictDate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String queryRoutePredictDate(Integer configType, Integer bizzType, String startSiteNode, String toSiteNode, Date pickUpEndTime) {
        String resultDate = "";
        try {

            BaseDto baseDto = new BaseDto();
            baseDto.setToken(vrsRouteTransferRelationApiToken);

            BnetPerFormanceConfigJsfReq bnetPerFormanceConfigJsfReq = new BnetPerFormanceConfigJsfReq();
            bnetPerFormanceConfigJsfReq.setConfigType(configType);
            bnetPerFormanceConfigJsfReq.setBizzType(bizzType);
            bnetPerFormanceConfigJsfReq.setNodeType(ROUTE_INTER_NODE_TYPE_CHENG_SHI_BIAN_MA);
            bnetPerFormanceConfigJsfReq.setStartNode(startSiteNode);
            bnetPerFormanceConfigJsfReq.setToNode(toSiteNode);
            bnetPerFormanceConfigJsfReq.setPickUpEndTime(pickUpEndTime);
            CommonDto<String> commonDto = vrsBNetQueryApi.queryPerformanceTime(baseDto, bnetPerFormanceConfigJsfReq);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null || StringHelper.isEmpty(commonDto.getData())) {
                logger.warn("查询远程路由时效信息失败,参数列表：configType:" + configType + ",bizzType:" + bizzType + ",startSiteNode:" + startSiteNode + ",toSiteNode:" + toSiteNode);
                logger.warn("查询远程路由时效信息失败，返回消息：" + commonDto.getMessage());
                return null;
            } else {
                logger.debug("查询远程路由时效成功：" + commonDto.getData() + ",参数：configType:" + configType + ",bizzType:" + bizzType + ",startSiteNode:" + startSiteNode + ",toSiteNode:" + toSiteNode);
                if (StringHelper.isNotEmpty(commonDto.getData())) {
                    Date tempDate = PerformanceTimeUtil.parseToFormatDateMinute(pickUpEndTime, commonDto.getData());
                    resultDate = PerformanceTimeUtil.format(tempDate, PerformanceTimeUtil.FORMAT_DATE);
                }
                return resultDate;
            }
        } catch (Exception e) {
            logger.error("查询订单路由时效失败：" + e);
            return null;
        }
    }

    /**
     * 批次清零 主页面统计
     *
     * @param page
     * @param parameter
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.transferWaveMonitorAPI.noSendAndArrivedButNoCheckSum", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public PageDto<TransferWaveMonitorResp> getAbnormalTotal(PageDto<TransferWaveMonitorReq> page, TransferWaveMonitorReq parameter) {
        BaseDto baseDto = new BaseDto();
        baseDto.setToken(vrsRouteTransferRelationApiToken);
        try {
            CommonDto<PageDto<TransferWaveMonitorResp>> commonDto = transferWaveMonitorAPI.noSendAndArrivedButNoCheckSum(baseDto, page, parameter);
//            CommonDto<PageDto<TransferWaveMonitorResp>> commonDto =testGetMain(baseDto, page, parameter);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null) {
                logger.warn("批次清零异常统计失败,参数列表：page:" + JsonHelper.toJson(page) + ",parameter:" + JsonHelper.toJson(parameter));
                logger.warn("批次清零异常统计失败，返回消息：" + (commonDto == null ? "commonDto=null" : commonDto.getMessage()));
                return null;
            } else {
                logger.debug("批次清零异常统计成功：page：" + JsonHelper.toJson(page) + ",parameter:" + JsonHelper.toJson(parameter));
                return commonDto.getData();
            }
        } catch (Exception e) {
            logger.error("批次清零异常统计失败：" + e);
            return null;
        }

    }

    /**
     * 批次清零 未发货明细
     *
     * @param page
     * @param waveBusinessId
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.transferWaveMonitorAPI.getNoSendDetail", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public PageDto<TransferWaveMonitorDetailResp> getNoSendDetail(PageDto<TransferWaveMonitorDetailResp> page, String waveBusinessId) {
        BaseDto baseDto = new BaseDto();
        baseDto.setToken(vrsRouteTransferRelationApiToken);
        try {
            CommonDto<PageDto<TransferWaveMonitorDetailResp>> commonDto = transferWaveMonitorAPI.getNoSendDetail(baseDto, page, waveBusinessId);
//            CommonDto<PageDto<TransferWaveMonitorDetailResp>> commonDto = testGetInspection(baseDto, page, waveBusinessId);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null) {
                logger.warn("批次清零异常未发货明细统计失败,参数列表：page:" + JsonHelper.toJson(page) + ",waveBusinessId:" + waveBusinessId);
                logger.warn("批次清零异常未发货明细统计失败，返回消息：" + (commonDto == null ? "commonDto=null" : commonDto.getMessage()));
                return null;
            } else {
                logger.debug("批次清零异常未发货明统计成功：page：" + JsonHelper.toJson(page) + ",waveBusinessId:" + waveBusinessId);
                return commonDto.getData();
            }
        } catch (Exception e) {
            logger.error("批次清零异常未发货明统计失败：" + e);
            return null;
        }
    }

    /**
     * 批次清零 未验货明细
     *
     * @param page
     * @param waveBusinessId
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.transferWaveMonitorAPI.getArrivedButNoCheckDetail", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public PageDto<TransferWaveMonitorDetailResp> getArrivedButNoCheckDetail(PageDto<TransferWaveMonitorDetailResp> page, String waveBusinessId) {
        BaseDto baseDto = new BaseDto();
        baseDto.setToken(vrsRouteTransferRelationApiToken);
        try {
            CommonDto<PageDto<TransferWaveMonitorDetailResp>> commonDto = transferWaveMonitorAPI.getArrivedButNoCheckDetail(baseDto, page, waveBusinessId);
//            CommonDto<PageDto<TransferWaveMonitorDetailResp>> commonDto = testGetInspection(baseDto, page, waveBusinessId);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null) {
                logger.warn("批次清零异常未验明细统计失败,参数列表：page:" + JsonHelper.toJson(page) + ",waveBusinessId:" + waveBusinessId);
                logger.warn("批次清零异常未验明细细统计失败，返回消息：" + (commonDto == null ? "commonDto=null" : commonDto.getMessage()));
                return null;
            } else {
                logger.debug("批次清零异常未验明细统计成功：page：" + JsonHelper.toJson(page) + ",waveBusinessId:" + waveBusinessId);
                return commonDto.getData();
            }
        } catch (Exception e) {
            logger.error("批次清零异常未验明细统计失败：" + e);
            return null;
        }
    }

    /**
     * 根据运单号、网点编码获取班次信息
     *
     * @param waybillCode
     * @param nodeCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.vrsWaybillQueryAPI.queryWaveInfoByWaybillCodeAndNodeCode", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public String queryWaveInfoByWaybillCodeAndNodeCode(String waybillCode, Integer nodeCode) {

        //站点区域查出来
        BaseStaffSiteOrgDto org = baseMajorManager.getBaseSiteBySiteId(nodeCode);
        if (org==null){
            return null;
        }
        BaseDto baseDto = new BaseDto();
        baseDto.setToken(vrsRouteTransferRelationApiToken);
        try {
            CommonDto<Map<String, List<String>>> commonDto = vrsWaybillQueryAPI.queryWaveInfoByWaybillCodeAndNodeCode(baseDto, waybillCode, org.getDmsSiteCode());
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null) {
                logger.warn("查询班次失败,参数列表：waybillCode:" + waybillCode + ",nodeCode:" + nodeCode);
                logger.warn("查询班次失败，返回消息：" + (commonDto == null ? "commonDto=null" : commonDto.getMessage()));
                return null;
            } else {
                logger.debug("查询班次失败成功：waybillCode:" + waybillCode + ",nodeCode:" + nodeCode);
                Map<String, List<String>> map = commonDto.getData();
                List<String> list = map.get("realWaves");//planWaves，realWaves
                if (list != null && list.size() > 0) {
                    return list.get(list.size() - 1);//取最后一个
                } else {
                    logger.warn("查询班次为空,参数列表：waybillCode:" + waybillCode + ",nodeCode:" + nodeCode);
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("查询班次失败" + e);
            return null;
        }
    }

    CommonDto<PageDto<TransferWaveMonitorDetailResp>> testGetInspection(BaseDto token, PageDto<TransferWaveMonitorDetailResp> page, String waveBusinessId){
        CommonDto commonDto=new CommonDto();
        commonDto.setCode(1);
        PageDto pageDto=new PageDto();
        pageDto.setPageSize(10);
        pageDto.setCurrentPage(page.getCurrentPage());
        pageDto.setTotalPage(50);
        pageDto.setTotalRow(499);
        List<TransferWaveMonitorDetailResp> list= Lists.newArrayList();
        for (int i=0;i<10;i++){
            TransferWaveMonitorDetailResp transferWaveMonitorDetailResp=new TransferWaveMonitorDetailResp();
            transferWaveMonitorDetailResp.setEndCityName("上海"+i);
            transferWaveMonitorDetailResp.setEndNodeName("分拣中心"+i);
            transferWaveMonitorDetailResp.setEndOrgName("华东"+i);
            transferWaveMonitorDetailResp.setStartCityName("北京"+i);
            transferWaveMonitorDetailResp.setStartOrgName("华北"+i);
            transferWaveMonitorDetailResp.setStartNodeName("始发分拣中心"+i);
            transferWaveMonitorDetailResp.setWaybillCode("vvvvvv"+i);
            transferWaveMonitorDetailResp.setEndProvinceName("上海省"+i);
            transferWaveMonitorDetailResp.setStartProvinceName("北京省"+i);
            list.add(transferWaveMonitorDetailResp);
        }
        pageDto.setResult(list);
        commonDto.setData(pageDto);
        return commonDto;
    }
    CommonDto<PageDto<TransferWaveMonitorResp>> testGetMain(BaseDto token, PageDto<TransferWaveMonitorReq> page, TransferWaveMonitorReq parameter){
        CommonDto commonDto=new CommonDto();
        commonDto.setCode(1);
        PageDto pageDto=new PageDto();
        pageDto.setPageSize(10);
        pageDto.setCurrentPage(page.getCurrentPage());
        pageDto.setTotalPage(50);
        pageDto.setTotalRow(499);
        List<TransferWaveMonitorResp> list= Lists.newArrayList();
        for (int i=0;i<10;i++){
            TransferWaveMonitorResp transferWaveMonitorResp=new TransferWaveMonitorResp();
            transferWaveMonitorResp.setDateTime(new Date());
            transferWaveMonitorResp.setWaveBusinessId("wav"+i);
            transferWaveMonitorResp.setOrgName("华北");
            transferWaveMonitorResp.setPlanStartTime(new Date());
            transferWaveMonitorResp.setPlanEndTime(new Date());
            transferWaveMonitorResp.setSiteName("分拣中心"+i);
            transferWaveMonitorResp.setSiteCode("010Y100");
            transferWaveMonitorResp.setActualArriveNoInspection(499);
            transferWaveMonitorResp.setNoSendWaybillCount(500);
            transferWaveMonitorResp.setWaveCode("vvvvvvv"+i);
            list.add(transferWaveMonitorResp);
        }
        pageDto.setResult(list);
        commonDto.setData(pageDto);
        return commonDto;
    }
}
