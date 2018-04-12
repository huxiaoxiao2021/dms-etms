package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.vrs.dto.CommonDto;
import com.jd.etms.vrs.dto.RecommendRouteDto;
import com.jd.etms.vrs.dto.compute.RouteProduct;
import com.jd.etms.vrs.ws.VrsRouteTransferRelationApi;
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
    @Autowired
    private VrsRouteTransferRelationApi vrsRouteTransferRelationApi;

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
                logger.info("查询远程路由成功："+commonDto.getData().getRecommendRouting()+",参数：startNode:"+startNode+",endNodeCode:"+endNodeCode+",predictSendTime:"+predictSendTime.getTime()+",routeProduct:"+routeProduct);
                return commonDto.getData().getRecommendRouting();
            }
        } catch (Exception e) {
            logger.error("查询远程路由中转信息失败：" + e);
            return null;
        }

    }
}
