package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.external.jos.service.JosService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.packageToMq.service.IPushPackageToMqService;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnServiceImple;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.erp.ws.ErpQuerySafWS;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 逆向换单打印
 * Created by wangtingwei on 14-8-7.
 */
@Service("ReversePrintService")
public class ReversePrintServiceImpl implements ReversePrintService {

    private final Log logger= LogFactory.getLog(ReversePrintServiceImpl.class);
    @Autowired
    private TaskService taskService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private IPushPackageToMqService pushMqService;


    private static final String REVERSE_PRINT_MQ_TOPIC="bd_blocker_complete";

    private static final String REVERSE_PRINT_MQ_MESSAGE_CATEGORY="BLOCKER_QUEUE_DMS_REVERSE_PRINT";

    @Autowired
    private ErpQuerySafWS baseErpQuerySafWSInfoSafService;
    /**
     * 处理逆向打印数据
     * 【1：发送全程跟踪 2：写分拣中心操作日志】
     * @param domain 打印提交数据
     * @return
     */
    @Override
    public boolean handlePrint(ReversePrintRequest domain) {
        if(BusinessHelper.isPackageCode(domain.getOldCode())){
            domain.setOldCode(BusinessHelper.getWaybillCodeByPackageBarcode(domain.getOldCode()));
        }

        if(BusinessHelper.isPackageCode(domain.getNewCode())){
            domain.setNewCode(BusinessHelper.getWaybillCodeByPackageBarcode(domain.getNewCode()));
        }
        Task tTask = new Task();
        tTask.setKeyword1(domain.getOldCode());
        tTask.setCreateSiteCode(domain.getSiteCode());
        tTask.setCreateTime(new Date(domain.getOperateUnixTime()));
        tTask.setKeyword2(String.valueOf(1700));
        tTask.setReceiveSiteCode(0);
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_TRACK_REVERSE_PRINT);
        status.setWaybillCode(domain.getOldCode());
        status.setOperateTime(new Date(domain.getOperateUnixTime()));
        status.setOperator(domain.getStaffRealName());
        status.setOperatorId(domain.getStaffId());
        status.setRemark("换单打印，新运单号"+domain.getNewCode());
        status.setCreateSiteCode(domain.getSiteCode());
        status.setCreateSiteName(domain.getSiteName());
        tTask.setBody(JsonHelper.toJson(status));
        /**
         * 原外单添加换单全程跟踪
         */
        taskService.add(tTask, true);
        tTask.setKeyword1(domain.getNewCode());
        status.setWaybillCode(domain.getNewCode());
        status.setRemark("换单打印，原运单号"+domain.getOldCode());
        tTask.setBody(JsonHelper.toJson(status));
        /**
         * 新外单添加全程跟踪
         */
        taskService.add(tTask);
        this.logger.info(REVERSE_PRINT_MQ_TOPIC+createMqBody(domain.getOldCode())+domain.getOldCode());
        pushMqService.pubshMq(REVERSE_PRINT_MQ_TOPIC, createMqBody(domain.getOldCode()), domain.getOldCode());
        OperationLog operationLog=new OperationLog();
        operationLog.setCreateTime(new Date());
        operationLog.setRemark("【外单逆向换单打印】原单号："+domain.getOldCode()+"新单号："+domain.getNewCode());
        operationLog.setWaybillCode(domain.getOldCode());
        operationLog.setCreateUser(domain.getStaffRealName());
        operationLog.setCreateUserCode(domain.getStaffId());
        operationLog.setCreateSiteCode(domain.getSiteCode());
        operationLog.setCreateSiteName(domain.getSiteName());
        operationLogService.add(operationLog);
        return true;
    }

    /**
     * 根据原单号获取对应的新单号
     * 1.自营拒收：新运单规则：T+原运单号。调取运单来源：从运单处获取，调取运单新接口。
     * 2.外单拒收：新运单规则：生成新的V单。调取运单来源：1）从外单获得新外单单号。2）通过新外单单号从运单处调取新外单的信息。
     * 3.售后取件单：新运单规则：生成W单或VY单。调取运单来源：从运单处获取，调取运单新接口。
     * 4.配送异常类订单：新运单规则：T+原运单号,调取运单来源：从运单处获得，调取运单新接口。
     * 5.返单换单：1）新运单规则：F+原运单号  或  F+8位数字,调取运单来源：从运单处获得，调取运单新接口。2）分拣中心集中换单，暂时不做。
     * @param oldWaybillCode 原单号
     * @return
     */
    @Override
    public String getNewWaybillCode(String oldWaybillCode) {

        this.baseErpQuerySafWSInfoSafService.getChangeWaybillCode(oldWaybillCode).getData();
        return null;
    }


    private String createMqBody(String orderId) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
        sb.append("<OrderTaskInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        sb.append("<OrderId>");
        sb.append(orderId);
        sb.append("</OrderId>");
        sb.append("<OrderType>");
        sb.append(0);
        sb.append("</OrderType>");
        sb.append("<MessageType>");
        sb.append(REVERSE_PRINT_MQ_MESSAGE_CATEGORY);
        sb.append("</MessageType>");
        sb.append("<OperatTime>");
        sb.append(DateHelper.formatDateTime(new Date()));
        sb.append("</OperatTime>");
        sb.append("</OrderTaskInfo>");
        return sb.toString();
    }
}
