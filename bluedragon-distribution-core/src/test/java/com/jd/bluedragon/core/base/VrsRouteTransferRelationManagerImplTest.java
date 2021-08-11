package com.jd.bluedragon.core.base;

import com.jd.etms.api.common.enums.RouteProductEnum;
import junit.framework.TestCase;
import org.elasticsearch.common.recycler.Recycler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
public class VrsRouteTransferRelationManagerImplTest extends TestCase {

    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Test
    public void testQueryRecommendRoute() throws Exception{

        vrsRouteTransferRelationManager.queryRecommendRoute("1","",new Date(), RouteProductEnum.T1);
//        vrsRouteTransferRelationManager.queryRecommendRoute("2","",new Date(), RouteProductEnum.T1);
//        vrsRouteTransferRelationManager.queryRecommendRoute("3","",new Date(), RouteProductEnum.T1);
//        vrsRouteTransferRelationManager.queryRecommendRoute("1","",new Date(), RouteProductEnum.T1);
//        vrsRouteTransferRelationManager.queryRecommendRoute("1","",new Date(), RouteProductEnum.T1);
//        vrsRouteTransferRelationManager.queryRecommendRoute("1","",new Date(), RouteProductEnum.T1);
//        vrsRouteTransferRelationManager.queryRecommendRoute("","",new Date(), RouteProductEnum.T1);
//        vrsRouteTransferRelationManager.queryRecommendRoute("1","",new Date(), RouteProductEnum.T1);
//        Thread.sleep(3000);
//        vrsRouteTransferRelationManager.queryRecommendRoute("4","",new Date(), RouteProductEnum.T1);
//        vrsRouteTransferRelationManager.queryRecommendRoute("5","",new Date(), RouteProductEnum.T1);
//        Thread.sleep(6000);
////        Thread.sleep(2000);
//        vrsRouteTransferRelationManager.queryRecommendRoute("6","",new Date(), RouteProductEnum.T1);
//        Thread.sleep(2000);
//        vrsRouteTransferRelationManager.queryRecommendRoute("1","",new Date(), RouteProductEnum.T1);
//        Thread.sleep(2000);
//        vrsRouteTransferRelationManager.queryRecommendRoute("1","",new Date(), RouteProductEnum.T1);
//        Thread.sleep(2000);
//        vrsRouteTransferRelationManager.queryRecommendRoute("1","",new Date(), RouteProductEnum.T1);
////        Thread.sleep(2000);
//        vrsRouteTransferRelationManager.queryRecommendRoute("","",new Date(), RouteProductEnum.T1);

    }
}