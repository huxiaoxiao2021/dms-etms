package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.dms.utils.WaybillUtil;
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
import com.jd.etms.api.waybillroutelink.WaybillRouteLinkCustAPI;
import com.jd.etms.api.waybillroutelink.req.WaybillRouteLinkConditionReq;
import com.jd.etms.api.waybillroutelink.resp.WaybillRouteLinkCustDetailResp;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.sdk.compute.RouteComputeUtil;
import com.jd.etms.sdk.util.PerformanceTimeUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private static final Logger log = LoggerFactory.getLogger(VrsRouteTransferRelationManagerImpl.class);

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

    @Autowired
    private WaybillRouteLinkCustAPI waybillRouteLinkCustAPI;

    @Value("${jsf.router.token}")
    private String vrsRouteTransferRelationApiToken;

    @Override
    public String queryRecommendRoute(String startNode, String endNodeCode, Date predictSendTime, RouteProductEnum routeProduct) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.VrsRouteTransferRelationManagerImpl.queryRecommendRoute", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            CommonDto<RecommendRouteResp> commonDto = routeComputeUtil.queryRecommendRoute(startNode, endNodeCode, predictSendTime, routeProduct);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null || StringHelper.isEmpty(commonDto.getData().getRecommendRouting())) {
                log.warn("查询远程路由中转信息失败,参数列表：startNode:{},endNodeCode:{},predictSendTime:{},routeProduct:{}"
                        ,startNode,endNodeCode,predictSendTime.getTime(),routeProduct);
                log.warn("查询远程路由中转信息失败，返回消息：{}" , JsonHelper.toJson(commonDto));
                return null;
            } else {
                log.debug("查询远程路由成功：{},参数：startNode:{},endNodeCode:{},predictSendTime:{},routeProduct:{}"
                        ,commonDto.getData().getRecommendRouting(),startNode,endNodeCode,predictSendTime.getTime(), routeProduct);
                return commonDto.getData().getRecommendRouting();
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("查询远程路由中转信息异常,参数列表：startNode:{},endNodeCode:{},predictSendTime:{},routeProduct:{}"
                    ,startNode,endNodeCode,predictSendTime.getTime(),routeProduct,e);
            return null;
        }finally {
            Profiler.registerInfoEnd(info);
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
    public String queryRoutePredictDate(Integer configType, Integer bizzType, String startSiteNode, String toSiteNode, Date pickUpEndTime) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.VrsRouteTransferRelationManagerImpl.queryRoutePredictDate", Constants.UMP_APP_NAME_DMSWEB,false, true);
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
                log.warn("查询远程路由时效信息失败,参数列表：configType:{},bizzType:{},startSiteNode:{},toSiteNode:{}"
                        ,configType,bizzType,startSiteNode, toSiteNode);
                log.warn("查询远程路由时效信息失败，返回消息：{}" , JsonHelper.toJson(commonDto));
                return null;
            } else {
                log.debug("查询远程路由时效成功：{},参数configType:{},bizzType:{},startSiteNode:{},toSiteNode:{}"
                        ,commonDto.getData(),configType,bizzType,startSiteNode, toSiteNode);
                if (StringHelper.isNotEmpty(commonDto.getData())) {
                    Date tempDate = PerformanceTimeUtil.parseToFormatDateMinute(pickUpEndTime, commonDto.getData());
                    resultDate = PerformanceTimeUtil.format(tempDate, PerformanceTimeUtil.FORMAT_DATE);
                }
                return resultDate;
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("查询远程路由时效信息异常,参数列表：configType:{},bizzType:{},startSiteNode:{},toSiteNode:{}"
                    ,configType,bizzType,startSiteNode, toSiteNode,e);
            return null;
        }finally {
            Profiler.registerInfoEnd(info);
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
    public PageDto<TransferWaveMonitorResp> getAbnormalTotal(PageDto<TransferWaveMonitorReq> page, TransferWaveMonitorReq parameter) {
        BaseDto baseDto = new BaseDto();
        baseDto.setToken(vrsRouteTransferRelationApiToken);
        CallerInfo info = Profiler.registerInfo("DMS.BASE.transferWaveMonitorAPI.noSendAndArrivedButNoCheckSum", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            CommonDto<PageDto<TransferWaveMonitorResp>> commonDto = transferWaveMonitorAPI.noSendAndArrivedButNoCheckSum(baseDto, page, parameter);
//            CommonDto<PageDto<TransferWaveMonitorResp>> commonDto =testGetMain(baseDto, page, parameter);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null) {
                log.warn("批次清零异常统计失败,参数列表：page:{},parameter:{}" ,JsonHelper.toJson(page), JsonHelper.toJson(parameter));
                log.warn("批次清零异常统计失败，返回消息：{}",JsonHelper.toJson(commonDto));
                return null;
            } else {
                if(log.isDebugEnabled()){
                    log.debug("批次清零异常统计成功：page：{},parameter:{}" ,JsonHelper.toJson(page), JsonHelper.toJson(parameter));
                }
                return commonDto.getData();
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("批次清零异常统计异常,参数列表：page:{},parameter:{}" ,JsonHelper.toJson(page), JsonHelper.toJson(parameter),e);
            return null;
        }finally {
            Profiler.registerInfoEnd(info);
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
    public PageDto<TransferWaveMonitorDetailResp> getNoSendDetail(PageDto<TransferWaveMonitorDetailResp> page, String waveBusinessId) {
        BaseDto baseDto = new BaseDto();
        baseDto.setToken(vrsRouteTransferRelationApiToken);
        CallerInfo info = Profiler.registerInfo("DMS.BASE.transferWaveMonitorAPI.getNoSendDetail", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            CommonDto<PageDto<TransferWaveMonitorDetailResp>> commonDto = transferWaveMonitorAPI.getNoSendDetail(baseDto, page, waveBusinessId);
//            CommonDto<PageDto<TransferWaveMonitorDetailResp>> commonDto = testGetInspection(baseDto, page, waveBusinessId);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null) {
                log.warn("批次清零异常未发货明细统计失败,参数列表：page:{},waveBusinessId:{}" ,JsonHelper.toJson(page), waveBusinessId);
                log.warn("批次清零异常未发货明细统计失败，返回消息：{}",JsonHelper.toJson(commonDto));
                return null;
            } else {
                if(log.isDebugEnabled()){
                    log.debug("批次清零异常未发货明统计成功：page：{},waveBusinessId:{}" ,JsonHelper.toJson(page), waveBusinessId);
                }
                return commonDto.getData();
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("批次清零异常未发货明细统计异常,参数列表：page:{},waveBusinessId:{}" ,JsonHelper.toJson(page), waveBusinessId,e);
            return null;
        }finally {
            Profiler.registerInfoEnd(info);
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
    public PageDto<TransferWaveMonitorDetailResp> getArrivedButNoCheckDetail(PageDto<TransferWaveMonitorDetailResp> page, String waveBusinessId) {
        BaseDto baseDto = new BaseDto();
        baseDto.setToken(vrsRouteTransferRelationApiToken);
        CallerInfo info = Profiler.registerInfo("DMS.BASE.transferWaveMonitorAPI.getArrivedButNoCheckDetail", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {//调用路由 查疑似未到的单子 说更准
            CommonDto<PageDto<TransferWaveMonitorDetailResp>> commonDto = transferWaveMonitorAPI.getMayNoArriveDetail(baseDto, page, waveBusinessId);
//            CommonDto<PageDto<TransferWaveMonitorDetailResp>> commonDto = testGetInspection(baseDto, page, waveBusinessId);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null) {
                log.warn("批次清零异常未验明细统计失败,参数列表：page:{},waveBusinessId:{}" ,JsonHelper.toJson(page), waveBusinessId);
                log.warn("批次清零异常未验明细细统计失败，返回消息：{}", JsonHelper.toJson(commonDto));
                return null;
            } else {
                if(log.isDebugEnabled()){
                    log.debug("批次清零异常未验明细统计成功：page：{},waveBusinessId:{}" ,JsonHelper.toJson(page) , waveBusinessId);
                }
                return commonDto.getData();
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("批次清零异常未验明细统计异常,参数列表：page:{},waveBusinessId:{}" ,JsonHelper.toJson(page), waveBusinessId,e);
            return null;
        }finally {
            Profiler.registerInfoEnd(info);
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
    public String queryWaveInfoByWaybillCodeAndNodeCode(String waybillCode, Integer nodeCode) {

        //站点区域查出来
        BaseStaffSiteOrgDto org = baseMajorManager.getBaseSiteBySiteId(nodeCode);
        if (org==null){
            return null;
        }
        BaseDto baseDto = new BaseDto();
        baseDto.setToken(vrsRouteTransferRelationApiToken);
        CallerInfo info = Profiler.registerInfo("DMS.BASE.vrsWaybillQueryAPI.queryWaveInfoByWaybillCodeAndNodeCode", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            CommonDto<Map<String, List<String>>> commonDto = vrsWaybillQueryAPI.queryWaveInfoByWaybillCodeAndNodeCode(baseDto, waybillCode, org.getDmsSiteCode());
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null) {
                log.warn("查询班次失败,参数列表：waybillCode:{},nodeCode:{}" ,waybillCode, nodeCode);
                log.warn("查询班次失败，返回消息：{}",JsonHelper.toJson(commonDto));
                return null;
            } else {
                log.debug("查询班次失败成功：waybillCode:{},nodeCode:{}" ,waybillCode, nodeCode);
                Map<String, List<String>> map = commonDto.getData();
                List<String> list = map.get("realWaves");//planWaves，realWaves
                if (list != null && list.size() > 0) {
                    return list.get(list.size() - 1);//取最后一个
                } else {
                    log.warn("查询班次为空,参数列表：waybillCode:{},nodeCode:{}" ,waybillCode, nodeCode);
                    return null;
                }
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("查询班次异常,参数列表：waybillCode:{},nodeCode:{}" ,waybillCode, nodeCode,e);
            return null;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    CommonDto<PageDto<TransferWaveMonitorDetailResp>> testGetInspection(BaseDto token, PageDto<TransferWaveMonitorDetailResp> page, String waveBusinessId){
        CommonDto commonDto=new CommonDto();
        commonDto.setCode(1);
        PageDto pageDto=new PageDto();
        pageDto.setPageSize(page.getPageSize());
        pageDto.setCurrentPage(page.getCurrentPage());
        pageDto.setTotalPage(50);
        pageDto.setTotalRow(499);
        List<TransferWaveMonitorDetailResp> list= Lists.newArrayList();
        for (int i=0;i<page.getPageSize();i++){
            TransferWaveMonitorDetailResp transferWaveMonitorDetailResp=new TransferWaveMonitorDetailResp();
            transferWaveMonitorDetailResp.setEndCityName("上海"+(page.getPageSize()*(page.getCurrentPage()-1) +i));
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


    /**
     * B网根据始发和目的获取路由信息
     * @param originalDmsCode 始发网点
     * @param destinationDmsCode 目的网点
     * @param routeProduct 产品类型
     * @param predictSendTime 预计发货时间
     * @return
     */
    public List<String> loadWaybillRouter(Integer originalDmsCode,Integer destinationDmsCode,RouteProductEnum routeProduct,Date predictSendTime){
        List<String> dmsSiteNameList = new ArrayList<String>();

        //校验参数
        if(originalDmsCode == null || destinationDmsCode == null || routeProduct == null || predictSendTime == null){
            return dmsSiteNameList;
        }
        //获取始发和目的的七位编码
        BaseStaffSiteOrgDto originalDms=baseMajorManager.getBaseSiteBySiteId(originalDmsCode);
        if (originalDms==null){
            return dmsSiteNameList;
        }
        BaseStaffSiteOrgDto destinationDms=baseMajorManager.getBaseSiteBySiteId(destinationDmsCode);
        if (destinationDms==null){
            return dmsSiteNameList;
        }

        //调路由的接口获取路由节点
        String router=queryRecommendRoute(originalDms.getDmsSiteCode(),destinationDms.getDmsSiteCode(),predictSendTime,routeProduct);

        if (StringUtils.isEmpty(router)){
            return dmsSiteNameList;
        }
        //拼接路由站点的名称
        String[] siteArr=router.split("\\|");
        //有路由节点的话，加上发出和接收节点，数量一定会>2个
        if (siteArr.length < 2){
            return dmsSiteNameList;
        }

        String preDmsName = "";
        for(int i=0;i<siteArr.length;i++){
            //获取站点信息
            BaseStaffSiteOrgDto baseStaffSiteOrgDto= baseMajorManager.getBaseSiteByDmsCode(siteArr[i]);
            if (baseStaffSiteOrgDto!=null){
                String dmsName = baseStaffSiteOrgDto.getDmsShortName();
                if(StringUtils.isBlank(dmsName)){
                    dmsName = baseStaffSiteOrgDto.getDmsName();
                }
                if(dmsName.equals(preDmsName)){
                    continue;
                }else{
                    preDmsName = dmsName;
                    dmsSiteNameList.add(dmsName);
                }
            }
        }
        return dmsSiteNameList;
    }

    /**
     * 查询路由节点信息
     *
     * @param packageCode 包裹号
     * @param siteCode 站点
     * @param operateType 操作类型
     * @see "https://cf.jd.com/pages/viewpage.action?pageId=295585165"
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.vrsWaybillQueryAPI.waybillRouteLinkQueryCondition", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "VrsRouteTransferRelationManagerImpl.waybillRouteLinkQueryCondition@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
    @Override
    public List<WaybillRouteLinkCustDetailResp> waybillRouteLinkQueryCondition(String packageCode,String siteCode,Integer operateType) {
        if(operateType == null|| StringUtils.isEmpty(packageCode)
                || StringUtils.isEmpty(siteCode)){
            log.warn("包裹号、计划网点、操作节点不能为空");
            return null;
        }
        WaybillRouteLinkConditionReq condition = new WaybillRouteLinkConditionReq();
        condition.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        condition.setPackageCode(packageCode);
        condition.setPlanNodeCode(siteCode);
        condition.setOperateType(operateType);
        CommonDto<List<WaybillRouteLinkCustDetailResp>> commonDto
                = waybillRouteLinkCustAPI.waybillRouteLinkQueryCondition(condition);
        if(commonDto != null && commonDto.getCode() == CommonDto.CODE_SUCCESS){
            return commonDto.getData();
        }else {
            log.warn("查询路由节点信息失败,异常信息:"+ commonDto==null?"":commonDto.getMessage());
            return null;
        }
    }
}
