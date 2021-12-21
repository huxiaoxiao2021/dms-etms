package com.jd.bluedragon.distribution.print.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.command.JdResults;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.print.service.PackagePrintInternalService;
import com.jd.bluedragon.distribution.print.waybill.handler.complete.PrintCompleteHandlerMapping;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wyh
 * @className PackagePrintInternalServiceImpl
 * @description
 * @date 2021/12/9 19:54
 **/
@Service("packagePrintInternalService")
public class PackagePrintInternalServiceImpl implements PackagePrintInternalService {

    private static final Logger log = LoggerFactory.getLogger(PackagePrintInternalServiceImpl.class);

    @Autowired
    private JdCommandService commandService;

    @Autowired
    @Qualifier("printCompleteHandlerMapping")
    private PrintCompleteHandlerMapping<JdCommand<PrintCompleteRequest>, JdResult<Boolean>> printCompleteHandlerMapping;

    /**
     * 获得包裹打印信息
     *
     * @param jsonCommand
     * @return
     */
    @Override
    public String getPrintInfo(String jsonCommand) {
        return commandService.execute(jsonCommand);
    }

    /**
     * 包裹打印完成回调
     *
     * @param request
     * @return
     */
    @Override
    public JdResult<Boolean> printComplete(JdCommand<PrintCompleteRequest> request) {
        JdResult<Boolean> jdResult = new JdResult<>();
        jdResult.toSuccess();

        Handler<JdCommand<PrintCompleteRequest>, JdResult<Boolean>> handler = printCompleteHandlerMapping.getHandler(request);
        if (handler == null) {
            //返回无服务信息
            jdResult = JdResults.REST_FAIL_SERVER_NOT_FIND;
            return jdResult;
        }
        try {

            jdResult = handler.handle(request);
        }
        catch (Exception e) {
            log.error("PackagePrintInternalServiceImpl.printComplete-error! request:{}", JsonHelper.toJson(request), e);
            jdResult = JdResults.REST_ERROR_SERVER_EXCEPTION;
        }

        return jdResult;
    }

}
