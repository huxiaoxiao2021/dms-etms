package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.api.bnet.VrsBNetQueryAPI;
import com.jd.etms.api.bnet.req.BnetPerFormanceConfigJsfReq;
import com.jd.etms.api.common.dto.BaseDto;
import com.jd.etms.api.common.dto.CommonDto;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.etms.api.recommendroute.resp.RecommendRouteResp;
import com.jd.etms.sdk.compute.RouteComputeUtil;
import com.jd.etms.sdk.util.PerformanceTimeUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    public void setRouteComputeUtil(RouteComputeUtil routeComputeUtil) {
        this.routeComputeUtil = routeComputeUtil;
    }

    @Autowired
    private VrsBNetQueryAPI vrsBNetQueryApi;

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
                logger.debug("查询远程路由成功：" + commonDto.getData().getRecommendRouting() + ",参数：startNode:" + startNode + ",endNodeCode:" + endNodeCode + ",predictSendTime:" + predictSendTime.getTime() + ",routeProduct:" + routeProduct);
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
}
