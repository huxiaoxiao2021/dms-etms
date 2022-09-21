package com.jd.bluedragon.distribution.sendprint.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineWaybillDTO;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sendprint.domain.*;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverListDto;

import java.util.List;

public interface SendPrintService {

    SummaryPrintResultResponse batchSummaryPrintQuery(PrintQueryCriteria criteria);

	BasicQueryEntityResponse basicPrintQuery(PrintQueryCriteria criteria);

	/**
	 * 分页查询交接清单明细
	 *
	 * @param criteria
	 * @return
	 */
	BasicQueryEntityResponse basicPrintQueryForPage(PrintQueryCriteria criteria);

	BasicQueryEntityResponse sopPrintQuery(PrintQueryCriteria criteria);

	BatchSendInfoResponse selectBoxBySendCode(List<BatchSend> batchSends);
	
	BasicQueryEntityResponse basicPrintQueryOffline(PrintQueryCriteria criteria);

	String getWaybillType(int waybillType);

	String getSendPay(int payment);

	/**
	 * 获取批次号打印所需对象数据信息
	 *
	 * @param criteria
	 * @return
	 */
	SendCodePrintEntity getSendCodePrintEntity(PrintQueryCriteria criteria);


	List<PrintOnlineWaybillDTO> queryWaybillCountBySendCode(String sendCode, Integer createSiteCode);

	/**
	 * 打印交接清单导出
	 * @param printExportCriteria
	 * @return
	 */
	InvokeResult<Boolean> batchPrintExport(PrintExportCriteria printExportCriteria);

	/**
	 * 打印交接清单导出到三方邮件
	 * @param tripartiteEntity 包含导出的条件
	 * @param content 邮件主题
	 * @param tos 邮件发送方
	 * @param ccs 邮件结果抄送方
	 * @return
	 */
	InvokeResult<Boolean> batchPrintExportToTripartite(TripartiteEntity tripartiteEntity, String content, List<String> tos, List<String> ccs);

	/**
	 * 构建打印交接清单实体
	 * @return
	 */
    PrintHandoverListDto buildPrintHandoverListDto(SendDetail sendDetail);
}
