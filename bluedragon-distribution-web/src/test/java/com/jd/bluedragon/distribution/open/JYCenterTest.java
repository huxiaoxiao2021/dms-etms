package com.jd.bluedragon.distribution.open;

import com.jd.bluedragon.distribution.open.entity.BatchInspectionPageRequest;
import com.jd.bluedragon.distribution.test.AbstractTestCase;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open
 * @ClassName: JYCenterTest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/12 11:12
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class JYCenterTest extends AbstractTestCase {

    @Autowired
    private JYCenterService jyCenterService;

    @Test
    public void testInspection() {
        String string = "{\n" +
                "    \"requestProfile\":{\n" +
                "        \"sysSource\":\"DPRH\",\n" +
                "        \"traceId\":\"23492374928374982\",\n" +
                "        \"timeZone\":\"GMT+8\",\n" +
                "        \"timestamp\":1670813988990" +
                "    },\n" +
                "    \"unloadDetailCargoList\":[\n" +
                "        {\n" +
                "            \"operateUserErp\":\"-1\",\n" +
                "            \"operateUserName\":\"真实名字\",\n" +
                "            \"operateTime\":1670813988990,\n" +
                "            \"barcode\":\"JD0003365401130-1-1-\",\n" +
                "            \"barcodeType\":\"1\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"operateSiteCode\":\"W0000027368\",\n" +
                "    \"operateSiteName\":\"嘉峪关运作部\",\n" +
                "    \"batchCode\":\"W0000027368-154705-2022112312456\",\n" +
                "    \"total\":\"1\",\n" +
                "    \"pageNo\":\"1\",\n" +
                "    \"pageSize\":\"200\"\n" +
                "}";

        jyCenterService.batchInspectionWithPage(JsonHelper.<BatchInspectionPageRequest>fromJsonUseGson(string, BatchInspectionPageRequest.class));


    }
}
