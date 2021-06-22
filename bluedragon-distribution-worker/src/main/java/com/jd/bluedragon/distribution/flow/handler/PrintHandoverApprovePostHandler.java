package com.jd.bluedragon.distribution.flow.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.core.base.FlowServiceManager;
import com.jd.bluedragon.core.base.PrintHandoverListManager;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.dms.wb.report.api.dto.base.BaseEntity;
import com.jd.dms.wb.report.api.dto.base.Pager;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverLitQueryCondition;
import com.jd.lsb.flow.domain.ApprovalResult;
import com.jd.lsb.flow.domain.ApproveRequestOrder;
import com.jd.lsb.flow.domain.HistoryApprove;
import com.jd.lsb.flow.handler.ApprovePostHandler;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * 打印交接清单审批结果监听处理器
 *
 * @author hujiping
 * @date 2021/4/21 7:44 下午
 */
@Service("printHandoverApprovePostHandler")
public class PrintHandoverApprovePostHandler implements ApprovePostHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PrintHandoverListManager printHandoverListManager;

    @Autowired
    private FlowServiceManager flowServiceManager;

    @Autowired
    private LogEngine logEngine;

    @JProfiler(jKey = "com.jd.bluedragon.distribution.flow.handler.PrintHandoverApprovePostHandler",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWORKER)
    @Override
    public void postApprove(HistoryApprove historyApprove) throws Throwable {
        if(logger.isInfoEnabled()){
            logger.info("审批回调结果：{}", JsonHelper.toJson(historyApprove));
        }
        if(historyApprove == null
                // 系统不一致
                || !Objects.equals(historyApprove.getSystem(),Constants.SYSTEM_NAME)
                // 应用不一致
                || !Objects.equals(historyApprove.getAppName(),Constants.SYS_DMS)
                // 流程编码不一致
                || !Objects.equals(historyApprove.getFlowName(), FlowConstants.FLOW_CODE_PRINT_HANDOVER)){
            // 测试时记录下日志，待上线时可删除
            logger.warn("非打印交接清单流程审批结果!审批结果【{}】", JsonHelper.toJson(historyApprove));
            return;
        }
        long startTime = System.currentTimeMillis();
        if(!Objects.equals(historyApprove.getState(), ApprovalResult.COMPLETE_AGREE.getValue())){
            logger.warn("申请人【{}】提交的打印交接清单导出流程未通过！审批结果【{}】、审批意见【{}】、审批工单号【{}】、审批节点编码【{}】",
                    historyApprove.getApplicant(), historyApprove.getState(), historyApprove.getComment(), historyApprove.getProcessInstanceNo(), historyApprove.getNodeName());
            addBusinessLog(startTime, historyApprove, null, false);
            return;
        }

        ApproveRequestOrder approveRequestOrder = flowServiceManager.getRequestOrder(historyApprove.getProcessInstanceNo());
        if(approveRequestOrder == null || approveRequestOrder.getArgs() == null){
            logger.warn("根据审批工单号【{}】未查询到打印交接单导出的审批流程!", historyApprove.getProcessInstanceNo());
            addBusinessLog(startTime, historyApprove, null, false);
            return;
        }
        Map<String, Object> argsMap = approveRequestOrder.getArgs();
        Map<String, Object> businessMap = JsonHelper.json2MapNormal(JsonHelper.toJson(argsMap.get(FlowConstants.FLOW_DATA_MAP_KEY_BUSINESS_DATA)));
        if(businessMap == null){
            logger.warn("根据申请单号【{}】未查询到设置的查询条件对象!", historyApprove.getProcessInstanceNo());
            addBusinessLog(startTime, historyApprove, null, false);
            return;
        }
        // fix：提交审批单时设置了才能获取
        String queryConditionJson = String.valueOf(businessMap.get(FlowConstants.FLOW_BUSINESS_QUERY_CONDITION));
        Pager<PrintHandoverLitQueryCondition> query = JSONObject.parseObject(queryConditionJson, new TypeReference<Pager<PrintHandoverLitQueryCondition>>(){});
        if(query == null){
            logger.warn("获取打印交接单导出审批流程的业务数据中的查询条件失败!");
            addBusinessLog(startTime, historyApprove, null, false);
            return;
        }
        // 异步导出发送至提交人咚咚
        boolean exportSuc = false;
        BaseEntity<Boolean> baseEntity = printHandoverListManager.doBatchExportAsync(query);
        if(baseEntity != null && Objects.equals(baseEntity.getData(),true)){
            logger.info("打印交接清单导出异步发咚咚至ERP【{}】成功!", query.getSearchVo().getUserCode());
            exportSuc = true;
        }
        // 记录businessLog
        addBusinessLog(startTime, historyApprove, query, exportSuc);
    }

    private void addBusinessLog(long startTime, HistoryApprove historyApprove, Pager<PrintHandoverLitQueryCondition> query, boolean exportSuc) {

        try {
            JSONObject logRequest = new JSONObject();
            logRequest.put("historyApprove", JsonHelper.toJson(historyApprove));

            JSONObject logResponse = new JSONObject();
            logResponse.put("approveResult",
                    "审批状态为：" + historyApprove.getState() +
                            "，审批结果：" +(Objects.equals(historyApprove.getState(),ApprovalResult.COMPLETE_AGREE.getValue()) ? "通过" : "未通过"));
            logResponse.put("approveResultExplain", "fix：0为待审批；1为节点审批通过；3为节点审批驳回；5为流程结束审批通过；6为流程结束审批驳回；8为流程结束未审批");
            logResponse.put("exportResult", exportSuc);
            logResponse.put("condition", query);

            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.APPROVAL_CALLBACK)
                    .methodName("PrintHandoverApprovePostHandler#postApprove")
                    .operateRequest(logRequest)
                    .operateResponse(logResponse)
                    .processTime(System.currentTimeMillis(),startTime)
                    .build();

            logEngine.addLog(businessLogProfiler);
        }catch (Exception e){
            logger.error("记录businessLog日志异常!", e);
        }

    }
}
