package com.jd.bluedragon.distribution.command;

import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;

public interface JdCommandService {
	/**
	 * 执行json格式的命令
	 * @param jsonCommand
	 * @return
	 */
	String execute(String jsonCommand);

    /**
     * 包裹打印完成回调
     * @param request
     * @return
     */
    JdResult<Boolean> printComplete(JdCommand<PrintCompleteRequest> request);
}
