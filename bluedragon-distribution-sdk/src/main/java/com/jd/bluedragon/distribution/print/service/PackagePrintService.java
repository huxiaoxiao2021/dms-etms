package com.jd.bluedragon.distribution.print.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackageImage;
import com.jd.bluedragon.distribution.print.request.PackagePrintRequest;

/**
 * 
 * @ClassName: PackagePrintService
 * @Description: 包裹标签打印服务接口
 * @author: wuyoude
 * @date: 2019年1月16日 上午10:58:50
 *
 */
public interface PackagePrintService {
	/**
	 * 获取包裹打印信息接口
	 * @param printRequest
	 * @return
	 */
	JdResult<Map<String,Object>> getPrintInfo(JdCommand<String> printRequest);
	/**
	 * 生成包裹打印图片信息接口
	 * @param printRequest
	 * @return
	 */
	JdResult<List<PrintPackageImage>> generateImage(JdCommand<String> printRequest);
}
