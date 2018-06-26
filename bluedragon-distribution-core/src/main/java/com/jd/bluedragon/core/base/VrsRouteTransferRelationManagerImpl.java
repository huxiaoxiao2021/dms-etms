package com.jd.bluedragon.core.base;

import java.util.Date;

import com.jd.etms.api.transferwavemonitor.req.TransferWaveMonitorReq;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorResp;
import com.jd.etms.vrs.dto.PageDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.vrs.dto.CommonDto;
import com.jd.etms.vrs.dto.RecommendRouteDto;
import com.jd.etms.vrs.dto.compute.RouteProduct;
import com.jd.etms.vrs.util.PerformanceTimeUtil;
import com.jd.etms.vrs.ws.VrsBNetQueryApi;
import com.jd.etms.vrs.ws.VrsRouteTransferRelationApi;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

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
    
    private static final Integer ROUTE_INTER_NODE_TYPE_CHENG_SHI_BIAN_MA= 0;
    
    private static final Integer ROUTE_INTER_NODE_TYPE_WANG_DIAN_BIAN_MA= 1;
    
    @Autowired
    private VrsRouteTransferRelationApi vrsRouteTransferRelationApi;
    
    @Autowired
    private VrsBNetQueryApi vrsBNetQueryApi;

    @Value("${jsf.router.token}")
    private String vrsRouteTransferRelationApiToken;

    @Override
    @JProfiler(jKey = "DMS.BASE.VrsRouteTransferRelationManagerImpl.queryRecommendRoute", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String queryRecommendRoute(String startNode, String endNodeCode, Date predictSendTime, RouteProduct routeProduct) {
        try {
            CommonDto<RecommendRouteDto> commonDto = vrsRouteTransferRelationApi.queryRecommendRoute(vrsRouteTransferRelationApiToken, startNode, endNodeCode, predictSendTime, routeProduct);
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null || StringHelper.isEmpty(commonDto.getData().getRecommendRouting())) {
                logger.warn("查询远程路由中转信息失败,参数列表：startNode:"+startNode+",endNodeCode:"+endNodeCode+",predictSendTime:"+predictSendTime.getTime()+",routeProduct:"+routeProduct);
                logger.warn("查询远程路由中转信息失败，返回消息：" + commonDto.getMessage());
                return null;
            }else{
                logger.debug("查询远程路由成功："+commonDto.getData().getRecommendRouting()+",参数：startNode:"+startNode+",endNodeCode:"+endNodeCode+",predictSendTime:"+predictSendTime.getTime()+",routeProduct:"+routeProduct);
                return commonDto.getData().getRecommendRouting();
            }
        } catch (Exception e) {
            logger.error("查询远程路由中转信息失败：" + e);
            return null;
        }

    }

	@Override
    @JProfiler(jKey = "DMS.BASE.VrsRouteTransferRelationManagerImpl.queryRoutePredictDate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String queryRoutePredictDate(Integer configType, Integer bizzType, String startSiteNode, String toSiteNode, Date pickUpEndTime) {
		String resultDate = "";
        try {
            /**
             * 查询B网应履约时效
             *
             * @param token         JSF Token
             * @param configType    配置类型
             * @param bizzType      业务类型
             * @param nodeType      网点类型 0、城市编码  1、网点编码
             * @param startNode   始发[城市/网点]编码
             * @param toNode      目的[城市/网点]编码
             * @param pickUpEndTime 预约揽收截止时间
             * @return 应履约时效
             */
            CommonDto<String> commonDto = vrsBNetQueryApi.queryPerformanceTime(vrsRouteTransferRelationApiToken, configType, bizzType, ROUTE_INTER_NODE_TYPE_CHENG_SHI_BIAN_MA, startSiteNode, toSiteNode, pickUpEndTime );
            if (commonDto == null || commonDto.getCode() != 1 || commonDto.getData() == null || StringHelper.isEmpty(commonDto.getData())) {
                logger.warn("查询远程路由时效信息失败,参数列表：configType:"+configType+",bizzType:"+bizzType+",startSiteNode:"+startSiteNode+",toSiteNode:"+toSiteNode);
                logger.warn("查询远程路由时效信息失败，返回消息：" + commonDto.getMessage());
                return null;
            }else{
                logger.debug("查询远程路由时效成功："+commonDto.getData()+",参数：configType:"+configType+",bizzType:"+bizzType+",startSiteNode:"+startSiteNode+",toSiteNode:"+toSiteNode);
                if(StringHelper.isNotEmpty(commonDto.getData())){
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

    @Override
    public PageDto<TransferWaveMonitorResp> getAbnormalTotal(PageDto<TransferWaveMonitorReq> page, TransferWaveMonitorReq parameter) {
        return null;
    }
}
