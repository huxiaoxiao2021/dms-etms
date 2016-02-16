package com.jd.bluedragon.distribution.rest.reverse;

import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by wangtingwei on 14-8-8.\src\main\webapp
 * <import resource="classpath:spring/distribution-core-context.xml" />
 <import resource="classpath:spring/distribution-web-mvc.xml" />
 <import resource="classpath:spring/distribution-web-authen.xml" />
 <import resource="classpath:spring/distribution-web-resteasy.xml" />
 <import resource="classpath:spring/distribution-web-wss-service.xml" />
 <import resource="classpath:spring/distribution-core-saf-server.xml" />
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class ReversePrintResourceTestCase {

    private static final Log logger= LogFactory.getLog(ReversePrintResourceTestCase.class);

    @Resource
    private ReversePrintResource rest;

    @BeforeClass
    public static void initClass(){

    }

    @Test
    public void handlePrintTest(){
        ReversePrintRequest request=new ReversePrintRequest();
        request.setNewCode("123456789");
        request.setOldCode("111222333");
        request.setStaffId(1);
        request.setStaffRealName("张三");
        request.setOperateUnixTime(new Date().getTime());
        request.setSiteCode(909);
        request.setSiteName("中关村自提点");
        logger.info("【测试换单打印】" + JsonHelper.toJson(request));

        InvokeResult<Boolean> result= rest.handlePrint(request);
        logger.info("【测试换单打印】"+JsonHelper.toJson(result));
        Assert.assertEquals(Boolean.TRUE,result.getData());

    }
}
