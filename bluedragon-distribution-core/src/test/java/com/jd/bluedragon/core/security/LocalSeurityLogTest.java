package com.jd.bluedragon.core.security;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineWaybillDTO;
import com.jd.bluedragon.distribution.sendprint.domain.BasicQueryEntity;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.bluedragon.test.BaseMockTest;
import com.jd.bluedragon.utils.LocalSecurityLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class LocalSeurityLogTest {




    @Test
    public void testLog(){
        PrintQueryCriteria request = new PrintQueryCriteria();

        request.setSiteCode(111);
        request.setReceiveSiteCode(111);
        request.setSendCode("sendCode");
        request.setStartTime("2022-07-07 12:12:12");
        request.setEndTime("2022-07-07 12:12:10");
        request.setBoxCode("boxcode");
        request.setSendUserCode(123);
        request.setPackageBarcode("packageCode");
        request.setWaybillcode("JDV0010101010");
        request.setFc(12);
        request.setIs211(false);
        request.setUserCode("chen");

        LocalSecurityLog.writeBatchSummaryPrintSecurityLog(LocalSeurityLogTest.class.getName(),request,summaryPrintResultResponse.getData());
    }


    private static SummaryPrintResultResponse summaryPrintResultResponse;

    private static List<BasicQueryEntity> tList = new ArrayList<>();

    static {
        BasicQueryEntity basicQueryEntity = new BasicQueryEntity();
        basicQueryEntity.setReceiverMobile("123123");
        basicQueryEntity.setReceiverName("zhangsan");
        basicQueryEntity.setReceiverAddress("北京丰台");
        basicQueryEntity.setSendUserCode(123);
        basicQueryEntity.setSendUser("李四");
        tList.add(basicQueryEntity);

        summaryPrintResultResponse = BaseMockTest.gson.fromJson("{\n" +
                "    \"code\": 200,\n" +
                "    \"message\": \"OK\",\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"details\": [\n" +
                "                {\n" +
                "                    \"boxCode\": \"JDVA00024194402-1-5-\",\n" +
                "                    \"waybillNum\": 1,\n" +
                "                    \"packageBarNum\": 1,\n" +
                "                    \"packageBarRecNum\": 1,\n" +
                "                    \"sealNo1\": \"\",\n" +
                "                    \"sealNo2\": \"\",\n" +
                "                    \"volume\": 0\n" +
                "                }\n" +
                "            ],\n" +
                "            \"sendSiteName\": \"北京马驹桥分拣中心\",\n" +
                "            \"receiveSiteName\": \"石景山营业部\",\n" +
                "            \"sendCode\": \"910-39-20190926162932012\",\n" +
                "            \"sendTime\": \"2019-09-26 16:29:35\",\n" +
                "            \"totalBoxNum\": 0,\n" +
                "            \"totalPackageNum\": 1,\n" +
                "            \"totalBoxAndPackageNum\": 1,\n" +
                "            \"totalShouldSendPackageNum\": 1,\n" +
                "            \"totalRealSendPackageNum\": 1,\n" +
                "            \"totalOutVolumeDynamic\": 0,\n" +
                "            \"totalOutVolumeStatic\": 0,\n" +
                "            \"totalInVolume\": 0\n" +
                "        }\n" +
                "    ]\n" +
                "}",new TypeToken<SummaryPrintResultResponse>(){}.getType());
    }

    @Test
    public void testwriteSummaryPrintSecurityLog(){
        PrintQueryCriteria request = new PrintQueryCriteria();

        request.setSiteCode(111);
        request.setReceiveSiteCode(111);
        request.setSendCode("sendCode");
        request.setStartTime("2022-07-07 12:12:12");
        request.setEndTime("2022-07-07 12:12:10");
        request.setBoxCode("boxcode");
        request.setSendUserCode(123);
        request.setPackageBarcode("packageCode");
        request.setWaybillcode("JDV0010101010");
        request.setFc(12);
        request.setIs211(false);
        request.setUserCode("chen");

        LocalSecurityLog.writeSummaryPrintSecurityLog(LocalSeurityLogTest.class.getName(),request,tList);
    }

}
