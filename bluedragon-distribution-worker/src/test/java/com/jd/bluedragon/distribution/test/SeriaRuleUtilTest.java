package com.jd.bluedragon.distribution.test;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.junit.Test;

import java.util.Date;

/**
 * Created by wuzuxiang on 2018/10/19.
 */
public class SeriaRuleUtilTest {

    @Test
    public void generateSendCodeTest() {
        String json = "{ \"barCode\" : \"80825823097N1S1H19\", \"registerNo\" : \"SH-BM-JD\", \"scannerTime\" : 1539850880000, \"source\" : 2, \"packageCode\" : \"80825823097N1S1H19\", \"chuteCode\" : \"CHU254\", \"distributeId\" : 3011, \"sendSiteCode\" : 509, \"operatorId\" : 140140, \"operatorName\" : \"刘玉蓉\" }\n";
        UploadData date = JsonHelper.fromJson(json,UploadData.class);

        long createSiteCode = 3011;
        long receiveSiteCode = 509;
        Date time = new Date(1539850880000L);

        String sendCode= SerialRuleUtil.generateSendCode(date.getDistributeId(),date.getSendSiteCode(),date.getScannerTime());
        System.out.println(sendCode);




    }
}
