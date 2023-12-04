package com.jd.bluedragon.distribution.consumer.jy;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.consumer.reassignWaybill.ReassignWaybillApprovalConsumer;
import com.jd.bluedragon.distribution.flow.handler.ReassignWaybillApprovePostHandler;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportReqDto;
import com.jd.jmq.common.message.Message;
import com.jd.lsb.flow.domain.HistoryApprove;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/13 16:29
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class ReassignWaybillApprovalConsumerTest {


    @Autowired
    private ReassignWaybillApprovalConsumer reassignWaybillApprovalConsumer;


    @Test
    public void consumerTest(){

        String body = "{\"applicationUserErp\":\"wuyoude\"," +
                "\"barCode\":\"JDVA00294399213-1-1-\"," +
                "\"changeSiteCode\":870194," +
                "\"changeSiteName\":\"青龙UAT站点3\"," +
                "\"changeSiteReasonType\":1," +
                "\"createUserErp\":\"wuyoude\"," +
                "\"receiveSiteCode\":39," +
                "\"receiveSiteName\":\"石景山营业部\"," +
                "\"submitTime\":\"2023-11-13 16:02:03\"," +
                "\"siteCode\":40240," +
                "\"siteName\":\"北京通州分拣中心\"}";
        Message message = new Message();
        message.setText(body);
        try {
            reassignWaybillApprovalConsumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Autowired
    private ReassignWaybillApprovePostHandler reassignWaybillApprovePostHandler;

    @Test
    public void dealApproveResultTest(){


        String str="{\n" +
                "  \"id\": 1216906,\n" +
                "  \"businessCode\": \"dms.etms\",\n" +
                "  \"flowId\": 2775,\n" +
                "  \"processInstanceNo\": \"reassign_waybill_approve-1-20231115145144-1724681572067577856\",\n" +
                "  \"nodeName\": \"firstApprove\",\n" +
                "  \"nodeDisplayName\": \"一级审批\",\n" +
                "  \"performType\": \"ANY\",\n" +
                "  \"applicant\": \"wuyoude\",\n" +
                "  \"createTime\": 1699951387000,\n" +
                "  \"system\": \"QLFJZXJT\",\n" +
                "  \"appName\": \"dms.etms\",\n" +
                "  \"flowName\": \"reassign_waybill_approve\",\n" +
                "  \"flowVersion\": 1,\n" +
                "  \"state\": 3,\n" +
                "  \"approver\": \"wuyoude\",\n" +
                "  \"approverName\": \"吴有德\",\n" +
                "  \"comment\": \"\",\n" +
                "  \"finishTime\": 1699951460038,\n" +
                "  \"dataMap\": {\n" +
                "    \"oa\": {\n" +
                "      \"jmeMainColList\": [\n" +
                "        \"单号:JDVA00294399213-1-1-\",\n" +
                "        \"原预分拣站点:石景山营业部\",\n" +
                "        \"反调度站点:青龙UAT站点3\",\n" +
                "        \"反调度原因:预分拣站点无法派送\",\n" +
                "        \"提报人:wuyoude\",\n" +
                "        \"提报场地:北京通州分拣中心\",\n" +
                "        \"提报时间:2023-11-13 16:02:03\"\n" +
                "      ],\n" +
                "      \"jmeReqComments\": \"返调度申请\",\n" +
                "      \"jmeReqName\": \"返调度申请单\"\n" +
                "    },\n" +
                "    \"bussinessData\": {\n" +
                "      \"businessNoKey\": \"JDVA00294399213-1-1-\"\n" +
                "    },\n" +
                "    \"flowControl\": {\n" +
                "      \"reassignWaybillApproveCount\": 2,\n" +
                "      \"preApprover\": \"wuyoude\",\n" +
                "      \"preApproveNodeState\": 1,\n" +
                "      \"reassignWaybillApproveFirstTriggerErp\": \"wuyoude\",\n" +
                "      \"reassignWaybillApproveSecondTriggerErp\": \"wuyoude\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"historyApprove\": true\n" +
                "}";


        HistoryApprove approve = JSONObject.parseObject(str, HistoryApprove.class);
        try {
            reassignWaybillApprovePostHandler.postApprove(approve);
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

}
