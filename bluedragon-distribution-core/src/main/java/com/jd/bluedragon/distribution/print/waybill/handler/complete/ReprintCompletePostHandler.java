package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.RePrintRecordMq;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.constants.DisposeNodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wyh
 * @className ReprintCompletePostHandler
 * @description
 * @date 2021/12/2 23:03
 **/
@Service("reprintCompletePostHandler")
public class ReprintCompletePostHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(ReprintCompletePostHandler.class);

    @Autowired
    @Qualifier("packageRePrintProducer")
    private DefaultJMQProducer packageRePrintProducer;

    /**
     * 拦截报表服务
     */
    @Autowired
    private IBusinessInterceptReportService businessInterceptReportService;

    @Autowired
    private BusinessInterceptConfigHelper businessInterceptConfigHelper;

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {

        PrintCompleteRequest request = context.getRequest();

        // 补打消息
        pushReprintMq(request);

        // 拦截记录
        sendDisposeAfterInterceptMsg(request);

        return context.getResult();
    }

    /**
     * 推送补打消息
     * @param request
     */
    private void pushReprintMq(PrintCompleteRequest request) {
        RePrintRecordMq rePrintRecordMq = new RePrintRecordMq();
        rePrintRecordMq.setOperateType(WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType());
        rePrintRecordMq.setWaybillCode(request.getWaybillCode());
        rePrintRecordMq.setPackageCode(request.getPackageBarcode());
        rePrintRecordMq.setTemplateGroupCode(request.getTemplateGroupCode());
        rePrintRecordMq.setTemplateName(request.getTemplateName());
        rePrintRecordMq.setTemplateVersion(request.getTemplateVersion());
        rePrintRecordMq.setUserCode(request.getOperatorCode());
        rePrintRecordMq.setUserName(request.getOperatorName());
        rePrintRecordMq.setUserErp(request.getOperatorErp());
        rePrintRecordMq.setSiteCode(request.getOperateSiteCode());
        rePrintRecordMq.setSiteName(request.getOperateSiteName());
        rePrintRecordMq.setOperateTime(new Date());

        packageRePrintProducer.sendOnFailPersistent(rePrintRecordMq.getWaybillCode(), JsonHelper.toJson(rePrintRecordMq));
    }

    /**
     * 发送消息
     * @param rePrintCallBackRequest 换单请求参数
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-14 14:11:58 周一
     */
    private Response<Boolean> sendDisposeAfterInterceptMsg(PrintCompleteRequest rePrintCallBackRequest){
        logger.info("PackageResource sendDisposeAfterInterceptMsg sendDisposeAfterInterceptMsg {}", JsonHelper.toJson(rePrintCallBackRequest));
        Response<Boolean> result = new Response<>();
        result.toSucceed();

        try {
            SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
            saveDisposeAfterInterceptMsgDto.setBarCode(rePrintCallBackRequest.getPackageBarcode());
            saveDisposeAfterInterceptMsgDto.setDisposeNode(businessInterceptConfigHelper.getDisposeNodeByConstants(DisposeNodeConstants.REPRINT));
            saveDisposeAfterInterceptMsgDto.setOperateTime(System.currentTimeMillis());
            saveDisposeAfterInterceptMsgDto.setOperateUserErp(rePrintCallBackRequest.getOperatorErp());
            saveDisposeAfterInterceptMsgDto.setOperateUserCode(rePrintCallBackRequest.getOperatorCode());
            saveDisposeAfterInterceptMsgDto.setOperateUserName(rePrintCallBackRequest.getOperatorName());
            saveDisposeAfterInterceptMsgDto.setSiteCode(rePrintCallBackRequest.getOperateSiteCode());
            saveDisposeAfterInterceptMsgDto.setSiteName(rePrintCallBackRequest.getOperateSiteName());
            businessInterceptReportService.sendDisposeAfterInterceptMsg(saveDisposeAfterInterceptMsgDto);
        }
        catch (Exception e) {
            logger.error("PackageResource sendDisposeAfterInterceptMsg exception, rePrintCallBackRequest: [{}]" , JsonHelper.toJson(rePrintCallBackRequest), e);
            result.toError("补打操作操作上报到拦截报表失败，失败提示：" + e.getMessage());
        }
        return result;
    }
}
