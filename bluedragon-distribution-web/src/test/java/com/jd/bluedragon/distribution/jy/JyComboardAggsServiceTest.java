package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author liwenji
 * @date 2022-11-16 17:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyComboardAggsServiceTest {

    @Autowired
    JyComboardAggsService jyComboardAggsService;
    @Autowired
    private JyComBoardSendService jyComBoardSendService;


    @Test
    public void testQueryBoardComboardAggs() throws Exception {

        JyComboardAggsEntity b = jyComboardAggsService.queryComboardAggs("888888888");
        System.out.println(JsonHelper.toJson(b));
    }

    @Test
    public void testQueryComboardAggs() throws Exception {
        JyComboardAggsEntity jyComboardAggsEntity = jyComboardAggsService.queryComboardAggs(910, 39);
        System.out.println(JsonHelper.toJson(jyComboardAggsEntity));

        JyComboardAggsEntity b = jyComboardAggsService.queryComboardAggs("B22120200000070");
        System.out.println(JsonHelper.toJson(b));

        List<JyComboardAggsEntity> c = jyComboardAggsService.queryComboardAggs("B22120200000070", UnloadProductTypeEnum.NONE);
        System.out.println(JsonHelper.toJson(c));
    }

    @Test
    public void testInsert(){
        Message msg = new Message();
        msg.setText("{\"bizId\":\"-1\",\"boardCode\":\"-500000000\",\"createTime\":1670140212085,\"jyAggsTypeEnum\":\"JY_COMBOARD_AGGS\",\"key\":\"40240-910--1--1--1--1\",\"operateSiteId\":\"40240\",\"productType\":\"-1\",\"receiveSiteId\":\"910\",\"scanType\":\"-1\",\"sendFlow\":\"40240-910\",\"waitScanCount\":1}");
        jyComboardAggsService.saveAggs(msg);
    }

    @Test
    public void testSortMachineComboard() throws Exception{
        String a = "{\n" +
                "\"currentOperate\" : {\n" +
                "\"siteCode\" : 125230,\n" +
                "\"siteName\" : \"上海松江分拣中心\",\n" +
                "\"operateTime\" : 1709799333000,\n" +
                "\"operatorTypeCode\" : 2,\n" +
                "\"operatorId\" : \"SHSJSH-CZ-ZDFJJ-SZJL-LF\",\n" +
                "\"operatorData\" : {\n" +
                "\"operatorTypeCode\" : 2,\n" +
                "\"operatorId\" : \"SHSJSH-CZ-ZDFJJ-SZJL-LF\",\n" +
                "\"machineCode\" : \"SHSJSH-CZ-ZDFJJ-SZJL-LF\",\n" +
                "\"chuteCode\" : \"10\",\n" +
                "\"workGridKey\" : \"CDWG00000009234\"\n" +
                "}\n" +
                "},\n" +
                "\"user\" : {\n" +
                "\"userCode\" : 20915807,\n" +
                "\"userName\" : \"许运辉\",\n" +
                "\"userErp\" : \"xuyunhui3\"\n" +
                "},\n" +
                "\"requestId\" : \"e5a3ae1e568f4ca5830b25cf1d16e310\",\n" +
                "\"bizSource\" : \"DMS_AUTOMATIC_WORKER_SYS\",\n" +
                "\"barCode\" : \"JDVB28173021208-7-15-\",\n" +
                "\"endSiteId\" : 145559,\n" +
                "\"endSiteName\" : \"上海柘林营业部\",\n" +
                "\"supportMutilSendFlow\" : false,\n" +
                "\"needSkipSendFlowCheck\" : false,\n" +
                "\"boardCode\" : \"B24030700098492\",\n" +
                "\"sendCode\" : \"125230-145559-20240307163279842\",\n" +
                "\"bizId\" : \"CB24030700097863\",\n" +
                "\"cancelLastSend\" : false,\n" +
                "\"destinationId\" : 145559,\n" +
                "\"forceSendFlag\" : false,\n" +
                "\"needSkipWeakIntercept\" : false,\n" +
                "\"needIntercept\" : false\n" +
                "}";
        ComboardScanReq request = JsonHelper.fromJson(a, ComboardScanReq.class);
        jyComBoardSendService.sortMachineComboard(request);
    }
}
