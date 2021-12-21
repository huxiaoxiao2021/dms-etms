package com.jd.bluedragon.distribution.print.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackageImage;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.print.request.RePrintRecordRequest;
import com.jd.bluedragon.distribution.print.request.SiteTerminalPrintCompleteRequest;

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
	 * 不推荐使用，会有内存溢出风险
	 * 生成包裹打印图片信息接口
	 * @param printRequest
	 * @return
	 */
	@Deprecated
	JdResult<List<PrintPackageImage>> generateImage(JdCommand<String> printRequest);
	/**
	 * 校验运单是否已操作补打
	 * @param rePrintRecordRequest
	 * @return
	 */
	JdResult<Boolean> hasReprintAll(JdCommand<RePrintRecordRequest> rePrintRecordRequest);
	/**
	 * 校验运单/包裹是否已操作补打
	 * @param rePrintRecordRequest
	 * @return
	 */
	JdResult<Boolean> hasReprinted(JdCommand<String> rePrintRecordRequest);
	/**
	 * 换单打印回调处理
	 * @param reversePrintAfterRequest
	 * @return
	 */
	JdResult<Boolean> reversePrintAfter(JdCommand<String> reversePrintAfterRequest);

	/**
	 * 现场预分拣获取展示数据接口
	 * @param packageCode 包裹号
	 * @return
	 */
	JdResult<String> getWaybillInfoForBackSchedule(JdCommand<String> packageCode);
	/**
	 * 现场预分拣回调处理
	 * @param reassignWaybillRequest
	 * @return
	 */
	JdResult<Boolean> backScheduleAfter(JdCommand<String> reassignWaybillRequest);
	/**
	 * 包裹补打回调处理
	 * @param reprintAfterRequest
	 * @return
	 */
	JdResult<Boolean> reprintAfter(JdCommand<String> reprintAfterRequest);

    /**
     * 打印完成回调接口
     * @param printRequest
     * @return
     */
	JdResult<Boolean> packagePrintComplete(JdCommand<SiteTerminalPrintCompleteRequest> printRequest);

}
