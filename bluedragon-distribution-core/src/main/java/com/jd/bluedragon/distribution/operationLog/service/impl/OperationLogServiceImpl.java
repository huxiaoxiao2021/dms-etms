package com.jd.bluedragon.distribution.operationLog.service.impl;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.log.BizOperateTypeConstants;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.operationLog.dao.OperationLogReadDao;
import com.jd.bluedragon.distribution.operationLog.dao.OperationlogCassandra;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.fastjson.JSONObject;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 1.不要删除此类；删除前请确认 logEngine接口保存的日志是否还需要或者迁移走。
 *
 * 2.不要使用此接口保存日志了。请使用统一的日志日志接口com.jd.bluedragon.distribution.log.impl.LogEngineImpl。
 * com.jd.bluedragon.distribution.log.impl.LogEngineImpl 此接口保存的日志会存储到business.log.jd.com 中;
 *
 * 3.等日日志都切到 logEngine接口,再删除 logCassandra接口保存日志的代码
 */
@Service
@Deprecated
public class OperationLogServiceImpl implements OperationLogService {

    private final static Logger log = LoggerFactory.getLogger(OperationLogServiceImpl.class);

	@Autowired
	private OperationlogCassandra logCassandra;
	
	@Autowired
	private OperationLogReadDao operationLogReadDao;

	@Autowired
    private LogEngine logEngine;
	
	@Resource
	private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 记录log到cassandra中
     *
     * @param operationLog
     * @return int
     */
    public int add(OperationLog operationLog) {
        if(operationLog == null){
            return 0;
        }

        //排查日志使用 可删除
        if(isReturnGoodsCode(operationLog)){
            log.info("退货数据打印日志operationLog[{}]", JsonHelper.toJson(operationLog));
        }

        JSONObject request = new JSONObject();
        request.put("boxCode", operationLog.getBoxCode());
        request.put("waybillCode", operationLog.getWaybillCode());
        request.put("packageCode", operationLog.getPackageCode());
        request.put("operatorName", operationLog.getCreateUser());
        request.put("operatorCode", operationLog.getCreateUserCode());
        request.put("sendCode", operationLog.getSendCode());
        request.put("siteCode", operationLog.getReceiveSiteCode());

        JSONObject response=new JSONObject();
        response.put("取件单号",operationLog.getPickupCode());
        response.put("机构",operationLog.getOrgCode());
        response.put("创建站点编号",operationLog.getCreateSiteCode());
        response.put("创建站点名称",operationLog.getCreateSiteName());
        response.put("接收站点编号",operationLog.getReceiveSiteCode());
        response.put("接收站点名称",operationLog.getReceiveSiteName());

        BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                .operateRequest(request)
                .methodName(operationLog.getMethodName())
                .url(operationLog.getUrl())
                .operateResponse(response)
                .timeStamp(operationLog.getOperateTime() == null ? System.currentTimeMillis() : operationLog.getOperateTime().getTime())
                .reMark(operationLog.getRemark())
                .build();

        Integer logType = operationLog.getLogType();
        if (OperationLog.LOG_TYPE_TRANSFER.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.TRANSFER_TRANSFER.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.TRANSFER_TRANSFER.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_RECEIVE.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.RECEIVE_RECEIVE.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.RECEIVE_RECEIVE.getOperateTypeCode());
        } else if (OperationLog.LOG_TYPE_INSPECTION.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.INSPECT_INSPECT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.INSPECT_INSPECT.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_POP_PRINT.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.PRINT_PRINT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.PRINT_PRINT.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_PARTNER_WAY_BILL.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.DELIVERY_PARTNER_WAY_BILL.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.DELIVERY_PARTNER_WAY_BILL.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_SORTING_CANCEL.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.SORTING_CANCEL_SORTING.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.SORTING_CANCEL_SORTING.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_SORTING.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.SORTING_SORTING.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.SORTING_SORTING.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_DEPARTURE.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.CAR_DEPARTURE.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.CAR_DEPARTURE.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_SEND_DELIVERY.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.DELIVERY_DELIVERY.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.DELIVERY_DELIVERY.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_SEND_CANCELDELIVERY.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.DELIVERY_CANCEL_DELIVERY.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.DELIVERY_CANCEL_DELIVERY.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_AMS_REJECT.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.AFTERSALE_REVERSE_AFTERSALE_REJECT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.AFTERSALE_REVERSE_AFTERSALE_REJECT.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_AMS_REJECT_INSPECT.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.INSPECT_SALE_REVERSE_INSPECT_REJECT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.INSPECT_SALE_REVERSE_INSPECT_REJECT.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_AMS_RECEIVE.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.RECEIVE_REVERSE_AMS_RECEIVE.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.RECEIVE_REVERSE_AMS_RECEIVE.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_WMS_REJECT.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.WAREHOUSING_REVERSE_WAREHOUSING_REJECT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.WAREHOUSING_REVERSE_WAREHOUSING_REJECT.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_WMS_REJECT_INSPECT.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.INSPECT_REVERSE_WMS_REJECT_INSPECT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.INSPECT_REVERSE_WMS_REJECT_INSPECT.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_WMS_RECEIVE.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.RECEIVE_REVERSE_WMS_RECEIVE.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.RECEIVE_REVERSE_WMS_RECEIVE.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_SPWMS_REJECT.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.BACKUP_STORAGE_REVERSE_SPWMS_REJECT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.BACKUP_STORAGE_REVERSE_SPWMS_REJECT.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_SPWMS_REJECT_INSPECT.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.INSPECT_BACKUP_STORAGE_REVERSE_INSPECT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.INSPECT_BACKUP_STORAGE_REVERSE_INSPECT.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_SPWMS_RECEIVE.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.RECEIVE_BACKUP_STORAGE_REVERSE_RECEIPT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.RECEIVE_BACKUP_STORAGE_REVERSE_RECEIPT.getOperateTypeCode());

        } else if (OperationLog.TYPE_REVERSE_RECEIVE.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.RECEIVE_REVERSE_RECEIPT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.RECEIVE_REVERSE_RECEIPT.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_FASTREFUND.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.RETURNS_FASTREFUND.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.RETURNS_FASTREFUND.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_SORTING_INTERCEPT_LOG.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.SORTING_SORTING_INTERCEPT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.SORTING_SORTING_INTERCEPT.getOperateTypeCode());

        } else if (OperationLog.LOG_TYPE_CAN_GLOBAL.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.LOAD_CAN_GLOBAL.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.LOAD_CAN_GLOBAL.getOperateTypeCode());

        } else if (OperationLog.PRINT_PACKAGE.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.PRINT_PACKAGE_LABEL_PRINT.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.PRINT_PACKAGE_LABEL_PRINT.getOperateTypeCode());

        } else if (OperationLog.BOARD_COMBINATITON.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.BOARDCOMBINATION_BOARDCOMBINATION.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.BOARDCOMBINATION_BOARDCOMBINATION.getOperateTypeCode());

        } else if (OperationLog.DATA_EXCEPTION.equals(logType)) {
            businessLogProfiler.setBizType(BizOperateTypeConstants.EXCEPTIONS_DATA_EXCEPTIONS.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.EXCEPTIONS_DATA_EXCEPTIONS.getOperateTypeCode());
        } else {
            businessLogProfiler.setBizType(BizOperateTypeConstants.OTHER_OTHER.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.OTHER_OTHER.getOperateTypeCode());
        }

        logEngine.addLog(businessLogProfiler);


        //增加侵入式监控开始
        CallerInfo info = null;
        try {
            info = Profiler.registerInfo("DMSWEB.OperationLogService.add", false, true);
            if (uccPropertyConfiguration.getCassandraGlobalSwitch()) {
                logCassandra.batchInsert(operationLog);
            }
        } catch (Throwable e) {
            log.error("插入操作日志失败，失败信息为：{}", e.getMessage(), e);
            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        //监控结束
        return 1;
    }

    private boolean isReturnGoodsCode(OperationLog operationLog) {
        String strPrefix = "PI20";
        return (StringUtils.isNotEmpty(operationLog.getBoxCode()) && operationLog.getBoxCode().startsWith(strPrefix))
                || (StringUtils.isNotEmpty(operationLog.getPackageCode()) && operationLog.getPackageCode().startsWith(strPrefix))
                || (StringUtils.isNotEmpty(operationLog.getPickupCode()) && operationLog.getPickupCode().startsWith(strPrefix))
                || (StringUtils.isNotEmpty(operationLog.getSendCode()) && operationLog.getSendCode().startsWith(strPrefix))
                || (StringUtils.isNotEmpty(operationLog.getWaybillCode()) && operationLog.getWaybillCode().startsWith(strPrefix));
    }

    public List<OperationLog> queryByParams(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return operationLogReadDao.queryByParams(params);
    }

    public Integer totalSizeByParams(Map<String, Object> params) {
        return operationLogReadDao.totalSizeByParams(params);
    }

    public List<OperationLog> queryByCassandra(String code, String type, Pager<OperationLog> pager) {
        return logCassandra.getPage(code, type, pager);
    }

    @Override
    public int totalSize(String code, String type) {
        return logCassandra.totalSize(code, type);
    }

}
