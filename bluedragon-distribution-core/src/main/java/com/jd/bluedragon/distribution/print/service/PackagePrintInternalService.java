package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;

/**
 * @ClassName PackagePrintInternalService
 * @Description
 * @Author wyh
 * @Date 2021/12/9 19:52
 **/
public interface PackagePrintInternalService {

    /**
     * 获得包裹打印信息
     * @param jsonCommand
     * @return
     */
    String getPrintInfo(String jsonCommand);

    /**
     * 包裹打印完成回调
     * @param request
     * @return
     */
    JdResult<Boolean> printComplete(JdCommand<PrintCompleteRequest> request);
}
