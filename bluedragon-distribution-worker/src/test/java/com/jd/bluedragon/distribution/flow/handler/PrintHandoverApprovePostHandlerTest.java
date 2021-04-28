package com.jd.bluedragon.distribution.flow.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.core.base.FlowServiceManager;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.wb.report.api.dto.base.Pager;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverLitQueryCondition;
import com.jd.lsb.flow.domain.ApprovalResult;
import com.jd.lsb.flow.domain.ApproveRequestOrder;
import com.jd.lsb.flow.domain.HistoryApprove;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;


/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/4/22 10:17 上午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class PrintHandoverApprovePostHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(PrintHandoverApprovePostHandlerTest.class);

    @Autowired
    private PrintHandoverApprovePostHandler printHandoverApprovePostHandler;

    @Autowired
    private FlowServiceManager flowServiceManager;

    private String flowWorkNo = null;

    @Test
    public void startFlow(){

        Pager<PrintHandoverLitQueryCondition> query = new Pager<PrintHandoverLitQueryCondition>();
        PrintHandoverLitQueryCondition condition = new PrintHandoverLitQueryCondition();
        condition.setUserCode("bjxings");
        condition.setCreateSiteCode(910);
        condition.setReceiveSiteCode(39);
        condition.setSendStartTime(System.currentTimeMillis());
        condition.setSendEndTime(System.currentTimeMillis());

        query.setSearchVo(condition);
        query.setPageNo(1);
        query.setPageSize(10);

        // OA数据
        Map<String,Object> oaMap = new HashMap<>();
        oaMap.put(FlowConstants.FLOW_OA_JMEREQNAME,"打印交接清单导出申请单");
        oaMap.put(FlowConstants.FLOW_OA_JMEREQCOMMENTS,"打印交接清单导出申请单");
        List<String> mainColList = new ArrayList<>();
        oaMap.put(FlowConstants.FLOW_OA_JMEMAINCOLLIST,mainColList);
        mainColList.add("发货始发地:北京马驹桥分拣中心");
        mainColList.add("发货目的地:石景山营业部");
        mainColList.add("发货开始时间:" + DateHelper.formatDateTime(new Date()));
        mainColList.add("发货结束时间:" + DateHelper.formatDateTime(new Date()));


        // 业务数据
        Map<String,Object> businessMap = new HashMap<>();
        // 设置业务唯一编码
        businessMap.put(FlowConstants.FLOW_BUSINESS_NO_KEY,FlowConstants.FLOW_CODE_PRINT_HANDOVER + Constants.SEPARATOR_VERTICAL_LINE
                + FlowConstants.FLOW_VERSION + Constants.SEPARATOR_VERTICAL_LINE + System.currentTimeMillis());
        // 设置业务的查询条件
        businessMap.put(FlowConstants.FLOW_BUSINESS_QUERY_CONDITION, JsonHelper.toJson(query));

        // 提交申请单
        flowWorkNo = flowServiceManager.startFlow(oaMap, businessMap, null,
                FlowConstants.FLOW_CODE_PRINT_HANDOVER, condition.getUserCode(), String.valueOf(businessMap.get(FlowConstants.FLOW_BUSINESS_NO_KEY)));

        // 查询申请单
        ApproveRequestOrder result = flowServiceManager.getRequestOrder(flowWorkNo);
        if(result != null && result.getArgs() != null
                && result.getArgs().get(FlowConstants.FLOW_DATA_MAP_KEY_BUSINESS_DATA) != null){

            Map<String, Object> businessMapResult = JsonHelper.json2MapNormal(JsonHelper.toJson(result.getArgs().get(FlowConstants.FLOW_DATA_MAP_KEY_BUSINESS_DATA)));
            logger.info("提交申请单时设置的查询条件：" + businessMapResult.get(FlowConstants.FLOW_BUSINESS_QUERY_CONDITION));
        }

        // 审批完成回调
        postApprove();
    }

    @Test
    public void postApprove() {
        try {
            HistoryApprove historyApprove = new HistoryApprove();
            historyApprove.setSystem(Constants.SYSTEM_NAME);
            historyApprove.setAppName(Constants.SYS_DMS);
            historyApprove.setFlowName(FlowConstants.FLOW_CODE_PRINT_HANDOVER);
            historyApprove.setState(ApprovalResult.AGREE.getValue());

            // 提交申请单后返回的申请单号
            historyApprove.setProcessInstanceNo(flowWorkNo);

            printHandoverApprovePostHandler.postApprove(historyApprove);

        }catch (Throwable e){
            logger.error("监听流程结果处理异常!", e);
            Assert.fail();
        }
    }
}