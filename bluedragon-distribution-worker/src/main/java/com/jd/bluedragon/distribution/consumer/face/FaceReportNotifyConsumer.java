package com.jd.bluedragon.distribution.consumer.face;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.exceptionReport.billException.domain.ExpressBillExceptionReport;
import com.jd.bluedragon.distribution.exceptionReport.billException.service.ExpressBillExceptionReportService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 面单举报回传消息消费
 *
 * @author hujiping
 * @date 2022/6/9 10:12 AM
 */
@Service("faceReportNotifyConsumer")
public class FaceReportNotifyConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(FaceReportNotifyConsumer.class);

    @Autowired
    private ExpressBillExceptionReportService expressBillExceptionReportService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("FaceReportNotifyConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER,false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("面单举报回传消息dws_spot_check 非JSON格式，内容为【{}】", message.getText());
                return;
            }
            FaceReportNotifyMQ faceReportNotifyMQ = JsonHelper.fromJsonUseGson(message.getText(), FaceReportNotifyMQ.class);
            if (faceReportNotifyMQ == null) {
                logger.warn("面单举报回传消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            // 过滤来源：2代表来源分拣
            if(!Objects.equals(faceReportNotifyMQ.getSourceSystem(), 2) || !Objects.equals(faceReportNotifyMQ.getSourceBill(), 2)){
                return;
            }
            // 根据商家编码和举报类型更新
            ExpressBillExceptionReport query = new ExpressBillExceptionReport();
            query.setReportType(faceReportNotifyMQ.getSecondProblem());
            query.setTraderCode(faceReportNotifyMQ.getCompanyCode());
            query.setTraderName(faceReportNotifyMQ.getCompanyName());
            query.setTraderOrgCode(faceReportNotifyMQ.getRegionNo());
            query.setTraderOrgName(faceReportNotifyMQ.getRegionName());
            query.setSaleStaffErp(faceReportNotifyMQ.getOwnerPin());
            query.setOrderStatus(faceReportNotifyMQ.getStatusName());
            if(Objects.equals(faceReportNotifyMQ.getStatus(), 5)){
                // 驳回
                query.setRejectedReason(faceReportNotifyMQ.getOperationDesc());
            }
            expressBillExceptionReportService.updateByBusiCode(query);
        }catch(Exception e){
            Profiler.functionError(info);
            logger.error("处理面举报回传消息失败, 消息体:{}", message.getText(), e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    class FaceReportNotifyMQ {
        // 来源系统编码：2表示分拣
        private Integer sourceSystem;
        // 来源单据类型：2表示分拣
        private Integer sourceBill;
        // 来源单据号：由商家编码和举报类型组成
        private String sourceBillCode;
        // 客户编号
        private String companyCode;
        // 客户名称
        private String companyName;
        // 状态编码
        private Integer status;
        // 状态
        private String statusName;
        // 一级举报类型编码
        private Integer firstProblem;
        // 一级举报类型名称
        private String firstProblemName;
        // 二级举报类型编码
        private Integer secondProblem;
        // 二级举报类型名称
        private String secondProblemName;
        // 是否紧急
        private Boolean isUrgent;
        // 商家所属大区编码
        private Integer regionNo;
        // 商家所属大区名称
        private String regionName;
        // 销售人ERP
        private String ownerPin;
        // 当前操作描述：驳回时是驳回原因
        private String operationDesc;

        public Integer getSourceSystem() {
            return sourceSystem;
        }

        public void setSourceSystem(Integer sourceSystem) {
            this.sourceSystem = sourceSystem;
        }

        public Integer getSourceBill() {
            return sourceBill;
        }

        public void setSourceBill(Integer sourceBill) {
            this.sourceBill = sourceBill;
        }

        public String getSourceBillCode() {
            return sourceBillCode;
        }

        public void setSourceBillCode(String sourceBillCode) {
            this.sourceBillCode = sourceBillCode;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        public Integer getFirstProblem() {
            return firstProblem;
        }

        public void setFirstProblem(Integer firstProblem) {
            this.firstProblem = firstProblem;
        }

        public String getFirstProblemName() {
            return firstProblemName;
        }

        public void setFirstProblemName(String firstProblemName) {
            this.firstProblemName = firstProblemName;
        }

        public Integer getSecondProblem() {
            return secondProblem;
        }

        public void setSecondProblem(Integer secondProblem) {
            this.secondProblem = secondProblem;
        }

        public String getSecondProblemName() {
            return secondProblemName;
        }

        public void setSecondProblemName(String secondProblemName) {
            this.secondProblemName = secondProblemName;
        }

        public Boolean getUrgent() {
            return isUrgent;
        }

        public void setUrgent(Boolean urgent) {
            isUrgent = urgent;
        }

        public Integer getRegionNo() {
            return regionNo;
        }

        public void setRegionNo(Integer regionNo) {
            this.regionNo = regionNo;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public String getOwnerPin() {
            return ownerPin;
        }

        public void setOwnerPin(String ownerPin) {
            this.ownerPin = ownerPin;
        }

        public String getOperationDesc() {
            return operationDesc;
        }

        public void setOperationDesc(String operationDesc) {
            this.operationDesc = operationDesc;
        }
    }
}
