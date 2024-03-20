package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.constants.DisposeNodeConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 换单打印完成后处理
 * @author fanggang7
 * @time 2024-03-19 16:58:36 周二
 **/
@Service("exchangePrintCompletePostHandler")
public class ExchangePrintCompletePostHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(ExchangePrintCompletePostHandler.class);

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

        // 拦截记录
        sendDisposeAfterInterceptMsg(request);

        return context.getResult();
    }

    /**
     * 发送消息
     * @param request 换单请求参数
     * @return 发送结果
     * @author fanggang7
     * @time 2024-03-19 16:56:19 周二
     */
    private Response<Boolean> sendDisposeAfterInterceptMsg(PrintCompleteRequest request){
        logger.info("ExchangePrintCompletePostHandler sendDisposeAfterInterceptMsg sendDisposeAfterInterceptMsg {}", JsonHelper.toJson(request));
        Response<Boolean> result = new Response<>();
        result.toSucceed();

        try {
            SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
            saveDisposeAfterInterceptMsgDto.setBarCode(request.getPackageBarcode());
            if(StringUtils.isBlank(saveDisposeAfterInterceptMsgDto.getBarCode())){
                saveDisposeAfterInterceptMsgDto.setBarCode(request.getWaybillCode());
            }
            saveDisposeAfterInterceptMsgDto.setDisposeNode(businessInterceptConfigHelper.getDisposeNodeByConstants(DisposeNodeConstants.EXCHANGE_WAYBILL));
            saveDisposeAfterInterceptMsgDto.setOperateTime(System.currentTimeMillis());
            saveDisposeAfterInterceptMsgDto.setOperateUserErp(request.getOperatorErp());
            saveDisposeAfterInterceptMsgDto.setOperateUserCode(request.getOperatorCode());
            saveDisposeAfterInterceptMsgDto.setOperateUserName(request.getOperatorName());
            saveDisposeAfterInterceptMsgDto.setSiteCode(request.getOperateSiteCode());
            saveDisposeAfterInterceptMsgDto.setSiteName(request.getOperateSiteName());
            businessInterceptReportService.sendDisposeAfterInterceptMsg(saveDisposeAfterInterceptMsgDto);
        }
        catch (Exception e) {
            logger.error("ExchangePrintCompletePostHandler sendDisposeAfterInterceptMsg exception, request: [{}]" , JsonHelper.toJson(request), e);
            result.toError("换单打印操作上报到拦截报表失败，失败提示：" + e.getMessage());
        }
        return result;
    }
}
