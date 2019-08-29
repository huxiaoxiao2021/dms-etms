package com.jd.bluedragon.distribution.print.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintRequest;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintResponse;

/**
 * 
 * @ClassName: JdCloudPrintService
 * @Description: 云打印服务接口
 * @author: wuyoude
 * @date: 2019年8月14日 下午5:55:24
 *
 */
public interface JdCloudPrintService {
	/**
	 * 获取默认的pdf请求
	 * @return
	 */
	<M> JdCloudPrintRequest<M> getDefaultPdfRequest();
	/**
	 * 调用云打印执行
	 * @param jdCloudPrintRequest
	 * @return
	 */
	<M> JdResult<List<JdCloudPrintResponse>> jdCloudPrint(JdCloudPrintRequest<M> jdCloudPrintRequest);
}
