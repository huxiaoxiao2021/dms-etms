package com.jd.bluedragon.distribution.test.box;

import com.jd.bluedragon.distribution.api.request.box.BoxRelationRequest;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.domain.BoxRelationQ;
import com.jd.bluedragon.distribution.box.jsf.BoxRelationJsfService;
import com.jd.bluedragon.distribution.box.service.BoxRelationService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.sdk.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @ClassName BoxRelationServiceImplTest
 * @Description
 * @Author wyh
 * @Date 2020/12/28 20:30
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class BoxRelationServiceImplTest {

    @Autowired
    private BoxRelationService boxRelationService;

    @Autowired
    private BoxRelationJsfService boxRelationJsfService;

    @Test
    public void queryBoxRelationTest() {
        BoxRelation boxRelation = new BoxRelation("BC010F002010Y10000126003", 910L);
        System.out.println(JsonHelper.toJson(boxRelationService.queryBoxRelation(boxRelation)));
        Assert.assertTrue(true);
    }

    @Test
    public void saveBoxRelationTest() {
        BoxRelationRequest request = new BoxRelationRequest();
        request.setUserErp("master");
        request.setBoxCode("BC010F002010Y10000126001");
        request.setRelationBoxCode("WJ1001201228180000000102");
        request.setUserName("master");
        request.setSiteCode(910);
        request.setOperateTime(DateUtil.format(new Date(), DateUtil.FORMAT_DATE_TIME));


        BoxRelation relation = BoxRelation.genEntity(request);
        System.out.println(JsonHelper.toJson(boxRelationService.saveBoxRelation(relation)));
        Assert.assertTrue(true);
    }

    @Test
    public void queryBindingDataTest() {
        BoxRelationQ query = new BoxRelationQ();
        query.setStartTime("2020-12-29 00:00:00");
        query.setEndTime("2021-1-30 00:00:00");
        query.setSiteCode(910L);
        System.out.println(JsonHelper.toJson(boxRelationJsfService.queryBindingData(query)));
    }
}
