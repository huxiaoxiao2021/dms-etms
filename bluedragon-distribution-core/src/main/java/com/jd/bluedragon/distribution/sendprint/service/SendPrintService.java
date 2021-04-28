package com.jd.bluedragon.distribution.sendprint.service;

import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineWaybillDTO;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sendprint.domain.BasicQueryEntityResponse;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendInfoResponse;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SendCodePrintEntity;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverListDto;

import java.util.List;

public interface SendPrintService {

    SummaryPrintResultResponse batchSummaryPrintQuery(PrintQueryCriteria criteria);
	SummaryPrintResultResponse newBatchSummaryPrintQuery(PrintQueryCriteria criteria);

	BasicQueryEntityResponse basicPrintQuery(PrintQueryCriteria criteria);

	/**
	 * 分页查询交接清单明细
	 *
	 * @param criteria
	 * @return
	 */
	BasicQueryEntityResponse basicPrintQueryForPage(PrintQueryCriteria criteria);

	BasicQueryEntityResponse newBasicPrintQuery(PrintQueryCriteria criteria);

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
	 * @param criteria
	 * @return
	 */
	BasicQueryEntityResponse basicPrintExport(PrintQueryCriteria criteria);

	/**
	 * 构建打印交接清单实体
	 * @return
	 */
    PrintHandoverListDto buildPrintHandoverListDto(SendDetail sendDetail);
}
