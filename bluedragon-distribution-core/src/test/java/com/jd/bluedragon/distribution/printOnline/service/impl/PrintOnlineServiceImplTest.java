package com.jd.bluedragon.distribution.printOnline.service.impl;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineWaybillDTO;
import com.jd.bluedragon.distribution.printOnline.service.IPrintOnlineService;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.test.BaseMockTest;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrintOnlineServiceImplTest {

    @InjectMocks
    private PrintOnlineServiceImpl printOnlineService;

    @Mock
    private SendPrintService sendPrintService;

    @Before
    public void before(){
        when(sendPrintService.batchSummaryPrintQuery(any(PrintQueryCriteria.class))).thenReturn(summaryPrintResultResponse);
        when(sendPrintService.queryWaybillCountBySendCode(anyString(),anyInt())).thenReturn(printOnlineWaybillDTOS);
    }

    @Test
    public void reversePrintOnline() {
        printOnlineService.reversePrintOnline("910-39-20190926162932012");

    }


    private static SummaryPrintResultResponse summaryPrintResultResponse;
    private static List<PrintOnlineWaybillDTO> printOnlineWaybillDTOS;

    static {
        printOnlineWaybillDTOS = BaseMockTest.gson.fromJson("[\n" +
                "        {\n" +

                "                \"waybillCode\": 1123454676,\n" +
                "                \"packageNum\": 1\n" +

                "        }\n" +
                "            ]",new TypeToken<List<PrintOnlineWaybillDTO>>(){}.getType());
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

}