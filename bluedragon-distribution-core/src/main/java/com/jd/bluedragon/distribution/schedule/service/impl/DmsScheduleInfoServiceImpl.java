package com.jd.bluedragon.distribution.schedule.service.impl;

import java.io.BufferedWriter;
import java.text.Collator;
import java.util.*;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.DmsScheduleInfoServiceManager;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnPickingExportDto;
import com.jd.bluedragon.distribution.storage.service.StoragePackageDService;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.jp.print.templet.center.sdk.common.SdkCommonResult;
import com.jd.jp.print.templet.center.sdk.dto.EdnDeliveryReceiptBatchPdfDto;
import com.jd.jp.print.templet.center.sdk.dto.EdnDeliveryReceiptBatchRequest;
import com.jd.jp.print.templet.center.sdk.service.KaGenerateEdnDeliveryReceiptPdfService;
import com.jd.ldop.utils.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.EdnServiceManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.log.BusinessLogDto;
import com.jd.bluedragon.distribution.log.service.BusinessLogManager;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintRequest;
import com.jd.bluedragon.distribution.print.service.JdCloudPrintService;
import com.jd.bluedragon.distribution.schedule.dao.DmsScheduleInfoDao;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfoCondition;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnBatchVo;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnPickingVo;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans.OperateTypeEnum;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.print.utils.StringHelper;

import javax.annotation.Resource;

/**
 * @ClassName: DmsScheduleInfoServiceImpl
 * @Description: 分拣调度信息表，数据来源于运单和企配仓--Service接口实现
 * @author wuyoude
 * @date 2020年04月30日 09:15:52
 *
 */
@Service("dmsScheduleInfoService")
public class DmsScheduleInfoServiceImpl extends BaseService<DmsScheduleInfo> implements DmsScheduleInfoService {

	private static final Logger logger = LoggerFactory.getLogger(DmsScheduleInfoServiceImpl.class);
	@Autowired
	@Qualifier("dmsScheduleInfoDao")
	private DmsScheduleInfoDao dmsScheduleInfoDao;
	
	@Autowired
	@Qualifier("jdCloudPrintService")
	private JdCloudPrintService jdCloudPrintService;
	
	@Autowired
	@Qualifier("sysConfigService")
	private SysConfigService sysConfigService;
	
	@Autowired
	@Qualifier("ednServiceManager")
	private EdnServiceManager ednServiceManager;
	
	@Autowired
	@Qualifier("businessLogManager")
	private BusinessLogManager businessLogManager;

	@Autowired
	@Qualifier("storagePackageDService")
	private StoragePackageDService storagePackageDService;

	@Autowired
	private ExportConcurrencyLimitService exportConcurrencyLimitService;

	@Resource
	private DmsScheduleInfoServiceManager  dmsScheduleInfoServiceManager;

	@Override
	public Dao<DmsScheduleInfo> getDao() {
		return this.dmsScheduleInfoDao;
	}

	@Override
	public boolean syncScheduleInfoToDb(DmsScheduleInfo dmsScheduleInfo) {
		DmsScheduleInfo oldData = dmsScheduleInfoDao.queryByWaybillCode(dmsScheduleInfo.getWaybillCode());
		if(oldData == null){
			return dmsScheduleInfoDao.insert(dmsScheduleInfo);
		}else if(DateHelper.compare(dmsScheduleInfo.getScheduleTime(), oldData.getScheduleTime())>0){
			dmsScheduleInfo.setId(oldData.getId());
			return dmsScheduleInfoDao.updateScheduleInfo(dmsScheduleInfo);
		}else{
			logger.warn("调度时间小于历史数据，不做更新！oldData[{}],newData[{}]", JsonHelper.toJson(oldData),JsonHelper.toJson(dmsScheduleInfo));
			return true;
		}
	}

	@Override
	public boolean syncEdnFahuoMsgToDb(DmsScheduleInfo dmsScheduleInfo) {
		DmsScheduleInfo oldData = dmsScheduleInfoDao.queryByWaybillCode(dmsScheduleInfo.getWaybillCode());
		if(oldData == null){
			return dmsScheduleInfoDao.insert(dmsScheduleInfo);
		}else if(DateHelper.compare(dmsScheduleInfo.getBusinessUpdateTime(), oldData.getBusinessUpdateTime())>0){
			dmsScheduleInfo.setId(oldData.getId());
			return dmsScheduleInfoDao.updateEdnFahuoInfo(dmsScheduleInfo);
		}else{
			logger.warn("操作时间小于历史数据，不做更新！oldData[{}],newData[{}]", JsonHelper.toJson(oldData),JsonHelper.toJson(dmsScheduleInfo));
		}
		return false;
	}

	@Override
	public PagerResult<DmsEdnPickingVo> queryEdnPickingListByPagerCondition(DmsScheduleInfoCondition dmsScheduleInfoCondition) {
		if(dmsScheduleInfoCondition != null){
			//设置调度时间条件
			if(StringHelper.isNotEmpty(dmsScheduleInfoCondition.getScheduleTimeGteStr())){
				dmsScheduleInfoCondition.setScheduleTimeGte(DateHelper.parseAllFormatDateTime(dmsScheduleInfoCondition.getScheduleTimeGteStr()));
			}
			if(StringHelper.isNotEmpty(dmsScheduleInfoCondition.getScheduleTimeLtStr())){
				dmsScheduleInfoCondition.setScheduleTimeLt(DateHelper.parseAllFormatDateTime(dmsScheduleInfoCondition.getScheduleTimeLtStr()));
			}
		}
		return dmsScheduleInfoDao.queryEdnPickingListByPagerCondition(dmsScheduleInfoCondition);
	}

	@Override
	public void export(DmsScheduleInfoCondition dmsScheduleInfoCondition, BufferedWriter bufferedWriter) {
		try {
			long start = System.currentTimeMillis();
			// 报表头
			Map<String, String> headerMap = getThisHeaderMap();
			//设置最大导出数量
			Integer MaxSize  =  exportConcurrencyLimitService.uccSpotCheckMaxSize();
			//设置单次导出数量
			Integer oneQuery = exportConcurrencyLimitService.getOneQuerySizeLimit();
			dmsScheduleInfoCondition.setLimit(oneQuery);
			CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());
			int queryTotal = 0;
			int index = 1;
			while (index <= (MaxSize/oneQuery)+1) {
				dmsScheduleInfoCondition.setOffset((index - 1) * oneQuery);
				index++;
				PagerResult<DmsEdnPickingVo> queryByPagerCondition = this.queryEdnPickingListByPagerCondition(dmsScheduleInfoCondition);
				if(CollectionUtils.isEmpty(queryByPagerCondition.getRows())){
					break;
				}
				List<DmsEdnPickingExportDto>  dataList   = transform(queryByPagerCondition.getRows());

				// 输出至csv文件中
				CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, dataList);
				// 限制导出数量
				queryTotal += dataList.size();
				if(queryTotal > MaxSize ){
					break;
				}
			}
			long end = System.currentTimeMillis();
			exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(dmsScheduleInfoCondition), ExportConcurrencyLimitEnum.DMS_SCHEDULE_INFO_REPORT.getName(),end-start,queryTotal);
		}catch (Exception e){
			log.error("企配仓拣货 export error",e);
		}
	}

	private List<DmsEdnPickingExportDto> transform(List<DmsEdnPickingVo> rows) {
		List<DmsEdnPickingExportDto> dataList = new ArrayList<>();
		for(DmsEdnPickingVo row : rows){
			DmsEdnPickingExportDto body = new DmsEdnPickingExportDto();
			body.setScheduleBillCode(row.getScheduleBillCode());
			body.setCarrierName(row.getCarrierName());
			body.setScheduleTime(DateHelper.formatDateTime(row.getScheduleTime()));
			dataList.add(body);
		}
		return dataList;
	}

	private Map<String, String> getThisHeaderMap() {
		Map<String,String> headerMap = new LinkedHashMap<>();
		headerMap.put("scheduleBillCode","调度单号");
		headerMap.put("carrierName","承运商");
		headerMap.put("scheduleTime","调度时间");
		return headerMap;
	}

	@Override
	public DmsEdnPickingVo queryDmsEdnPickingVo(String scheduleBillCode) {
		return dmsScheduleInfoDao.queryDmsEdnPickingVo(scheduleBillCode);
	}

	@Override
	public List<String> queryEdnBatchNumList(String scheduleBillCode) {
		return dmsScheduleInfoDao.queryEdnBatchNumList(scheduleBillCode);
	}

	@Override
	public List<DmsScheduleInfo> queryEdnDmsScheduleInfoList(
			String scheduleBillCode) {
		List<DmsScheduleInfo> dataList = dmsScheduleInfoDao.queryEdnDmsScheduleInfoList(scheduleBillCode);
		//设置序号值
		if(dataList != null && !dataList.isEmpty()){
			int rowNum = 1;
			for(DmsScheduleInfo item : dataList){
				item.setRowNum(rowNum ++);
				if(null!=item.getDestDmsSiteCode()){
					List<String> storageCodeList=storagePackageDService.queryStorageCodeByWaybillCodeAndSiteCode(item.getWaybillCode(),item.getDestDmsSiteCode().longValue());
					//判断多条包裹信息下面，是否储位号都为空
					if(!CollectionUtils.isEmpty(storageCodeList)) {
						item.setStorageCodes(StringUtils.join(storageCodeList,","));
					}
				}
			}
		}
		return dataList;
	}


	@Override
	public DmsEdnPickingVo queryDmsEdnPickingVoForView(String scheduleBillCode) {
		DmsEdnPickingVo dmsEdnPickingVo = this.queryDmsEdnPickingVo(scheduleBillCode);
		if(dmsEdnPickingVo != null){
			dmsEdnPickingVo.setDmsScheduleInfoList(this.queryEdnDmsScheduleInfoList(scheduleBillCode));
			//查询操作日志
			dmsEdnPickingVo.setDmsEdnOperateLogList(businessLogManager.queryLogs(scheduleBillCode));
		}
		return dmsEdnPickingVo;
	}

	@Override
	public JdResponse<String> printEdnPickingList(String scheduleBillCode,LoginUser user) {
		JdResponse<String> printResult = new JdResponse<String>();
		DmsEdnPickingVo dmsEdnPickingVo = this.queryDmsEdnPickingVo(scheduleBillCode);
		if(dmsEdnPickingVo != null){
			dmsEdnPickingVo.setDmsScheduleInfoList(this.queryEdnDmsScheduleInfoList(scheduleBillCode));
			//储位排序
			sortScheduleInfoByStorageCodes(dmsEdnPickingVo.getDmsScheduleInfoList());
			JdCloudPrintRequest<DmsEdnPickingVo> printRequest = jdCloudPrintService.getDefaultPdfRequest(true);
			printRequest.setOrderNum(scheduleBillCode);
			printRequest.setTemplate(DmsConstants.TEMPLATE_NAME_EDN_PICKING);
			SysConfig templateConfig = sysConfigService.findConfigContentByConfigName(DmsConstants.TEMPLATE_VERSION_KEY_EDN_PICKING);
			if(templateConfig != null){
				printRequest.setTemplateVer(templateConfig.getConfigContent());
			}else{
				printRequest.setTemplateVer(DmsConstants.TEMPLATE_VERSION_DEFAULT_EDN_PICKING);
			}
			List<DmsEdnPickingVo> printData = new ArrayList<DmsEdnPickingVo>();
			printData.add(dmsEdnPickingVo);
			printRequest.setModel(printData);
			JdResult<String> pdfResult = jdCloudPrintService.printPdfAndReturnWebUrl(printRequest);
			if(pdfResult != null && pdfResult.isSucceed()){
				printResult.setData(pdfResult.getData());
				//记录打印日志
				BusinessLogDto log = new BusinessLogDto();
				log.setBusinessKey(scheduleBillCode);
				log.setOperateType(OperateTypeEnum.EDN_PRINT_PICKING_LIST.getCode());
				if(user != null){
					log.setOperateUser(user.getUserErp());
				}
				log.setOperateContent(OperateTypeEnum.EDN_PRINT_PICKING_LIST.getText());
				//记录打印日志
				businessLogManager.addLog(log);
			}else {
				if(pdfResult != null){
					printResult.toFail(pdfResult.getMessage());
				}else{
					printResult.toFail("pdf生成失败！");
				}
			}
		}else{
			printResult.toFail("调度单不存在！");
		}
		return printResult;
	}

	private void sortScheduleInfoByStorageCodes(List<DmsScheduleInfo> dmsScheduleInfoList){
		if(org.apache.commons.collections.CollectionUtils.isNotEmpty(dmsScheduleInfoList)){
			//进行排序
			Collections.sort(dmsScheduleInfoList, new Comparator<DmsScheduleInfo>() {
				@Override
				public int compare(DmsScheduleInfo p1, DmsScheduleInfo p2) {
					if(null == p1.getStorageCodes() && null ==  p2.getStorageCodes()){
						return 0;
					}else if(null == p1.getStorageCodes()){
						return 1;
					}else if(null == p2.getStorageCodes()){
						return -1;
					}else {
						return p1.getStorageCodes().compareTo(p2.getStorageCodes());
					}
				}
			});
		}
		int rowNum = 1;
		for(DmsScheduleInfo dmsScheduleInfo : dmsScheduleInfoList){
			dmsScheduleInfo.setRowNum(rowNum ++);;
		}
	}

	@Override
	public JdResponse<List<DmsEdnBatchVo>> printEdnDeliveryReceipt(String scheduleBillCode,LoginUser user) {
		JdResponse<List<DmsEdnBatchVo>> printResult = new JdResponse<List<DmsEdnBatchVo>>();
		List<String> ednBatchNumList = this.queryEdnBatchNumList(scheduleBillCode);
		if(ednBatchNumList != null && ednBatchNumList.size() > 0){
			JdResult<List<DmsEdnBatchVo>> result = ednServiceManager.batchGetDeliveryReceipt(ednBatchNumList);
			if(result != null && result.isSucceed() && result.getData() != null){
				printResult.setData(result.getData());
			}else if(result != null){
				printResult.toFail(result.getMessageCode()+ result.getMessage());
			}else{
				printResult.toFail("调用企配仓接口失败！");
			}
			BusinessLogDto log = new BusinessLogDto();
			log.setBusinessKey(scheduleBillCode);
			log.setOperateType(OperateTypeEnum.EDN_PRINT_DELIVERY_RECEIPT.getCode());
			if(user != null){
				log.setOperateUser(user.getUserErp());
			}
			log.setOperateContent(OperateTypeEnum.EDN_PRINT_DELIVERY_RECEIPT.getText());
			//记录打印日志
			businessLogManager.addLog(log);
		}else{
			printResult.toFail("调度单下企配仓批次为空！");
		}
		return printResult;
	}



	@Override
	public JdResponse<EdnDeliveryReceiptBatchPdfDto> generatePdfUrlByBatchList(EdnDeliveryReceiptBatchRequest param) {
		if(logger.isInfoEnabled()){
			logger.info("com.jd.bluedragon.distribution.schedule.service.impl.DmsScheduleInfoServiceImpl--》generatePdfUrlByBatchList start ,param=[{}]",JsonHelper.toJson(param));
		}
		JdResponse<EdnDeliveryReceiptBatchPdfDto> response = new JdResponse<>();
		response.setCode(JdResponse.CODE_SUCCESS);

		if(StringUtils.isBlank(param.getScheduleBillCode())){
			logger.warn("com.jd.bluedragon.distribution.schedule.service.impl.DmsScheduleInfoServiceImpl--》generatePdfUrlByBatchList 调度单号不能为空 ,param=[{}]",JsonHelper.toJson(param));
			response.setCode(JdResponse.CODE_FAIL);
			response.setMessage("调度单号不能为空");
			return  response;
		}

		if(org.apache.commons.collections4.CollectionUtils.isEmpty(param.getEdnNos())){
			logger.warn("com.jd.bluedragon.distribution.schedule.service.impl.DmsScheduleInfoServiceImpl--》generatePdfUrlByBatchList 企配单批次号不能为空 ,param=[{}]",JsonHelper.toJson(param));
			response.setCode(JdResponse.CODE_FAIL);
			response.setMessage("企配单批次号不能为空");
			return  response;
		}

	 	return dmsScheduleInfoServiceManager.generatePdfUrlByBatchList(param);
	}
}
