package com.jd.bluedragon.distribution.schedule.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.EdnServiceManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintRequest;
import com.jd.bluedragon.distribution.print.service.JdCloudPrintService;
import com.jd.bluedragon.distribution.schedule.dao.DmsScheduleInfoDao;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfoCondition;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnBatchVo;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnOperateLog;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnPickingVo;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

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
	/**
	 * 导出excel头
	 */
	private static final List<Object> EXCELL_HEADS = new ArrayList<Object>();
	static {
		EXCELL_HEADS.add("调度单号");
		EXCELL_HEADS.add("承运商");
		EXCELL_HEADS.add("调度时间");
	}
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
		}
		return false;
	}

	@Override
	public boolean syncEdnFahuoMsgToDb(DmsScheduleInfo dmsScheduleInfo) {
		DmsScheduleInfo oldData = dmsScheduleInfoDao.queryByWaybillCode(dmsScheduleInfo.getWaybillCode());
		if(oldData == null){
			return dmsScheduleInfoDao.insert(dmsScheduleInfo);
		}else if(DateHelper.compare(dmsScheduleInfo.getScheduleTime(), oldData.getScheduleTime())>0){
			dmsScheduleInfo.setId(oldData.getId());
			return dmsScheduleInfoDao.updateEdnFahuoInfo(dmsScheduleInfo);
		}else{
			logger.warn("操作时间小于历史数据，不做更新！oldData[{}],newData[{}]", JsonHelper.toJson(oldData),JsonHelper.toJson(dmsScheduleInfo));
		}
		return false;
	}
	@Override
	public PagerResult<DmsEdnPickingVo> queryEdnPickingListByPagerCondition(DmsScheduleInfoCondition dmsScheduleInfoCondition) {
		return dmsScheduleInfoDao.queryEdnPickingListByPagerCondition(dmsScheduleInfoCondition);
	}
	@Override
	public List<List<Object>> queryEdnPickingExcelData(DmsScheduleInfoCondition dmsScheduleInfoCondition) {
		List<List<Object>> resList = new ArrayList<List<Object>>();
		resList.add(EXCELL_HEADS);
		dmsScheduleInfoCondition.setLimit(5000);
		PagerResult<DmsEdnPickingVo> queryByPagerCondition = this.queryEdnPickingListByPagerCondition(dmsScheduleInfoCondition);

		List<DmsEdnPickingVo> rows = queryByPagerCondition.getRows();

		for(DmsEdnPickingVo row : rows){
			List<Object> body = new ArrayList<Object>();
			body.add(row.getScheduleBillCode());
			body.add(row.getCarrierName());
			body.add(DateHelper.formatDate(row.getScheduleTime()));
			resList.add(body);
		}
		return resList;
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
		return dmsScheduleInfoDao.queryEdnDmsScheduleInfoList(scheduleBillCode);
	}

	@Override
	public DmsEdnPickingVo queryDmsEdnPickingVoForView(String scheduleBillCode) {
		DmsEdnPickingVo dmsEdnPickingVo = this.queryDmsEdnPickingVo(scheduleBillCode);
		if(dmsEdnPickingVo != null){
			dmsEdnPickingVo.setDmsScheduleInfoList(this.queryEdnDmsScheduleInfoList(scheduleBillCode));
			//查询操作日志
			dmsEdnPickingVo.setDmsEdnOperateLogList(new ArrayList<DmsEdnOperateLog>());
			DmsEdnOperateLog log1 = new DmsEdnOperateLog();
			log1.setOperateContent("打印交接单");
			log1.setOperateUser("test");
			log1.setOperateTime("2020-04-30 12:15:45");
			dmsEdnPickingVo.getDmsEdnOperateLogList().add(log1);
		}
		return dmsEdnPickingVo;
	}

	@Override
	public JdResponse<String> printEdnPickingList(String scheduleBillCode) {
		JdResponse<String> printResult = new JdResponse<String>();
		DmsEdnPickingVo dmsEdnPickingVo = this.queryDmsEdnPickingVo(scheduleBillCode);
		if(dmsEdnPickingVo != null){
			dmsEdnPickingVo.setDmsScheduleInfoList(this.queryEdnDmsScheduleInfoList(scheduleBillCode));
			JdCloudPrintRequest<DmsEdnPickingVo> printRequest = jdCloudPrintService.getDefaultPdfRequest();
			printRequest.setTemplate(DmsConstants.TEMPLATE_NAME_EDN_PICKING);
			SysConfig templateConfig = sysConfigService.findConfigContentByConfigName(DmsConstants.TEMPLATE_VERSION_KEY_EDN_PICKING);
			if(templateConfig != null){
				printRequest.setTemplateVer(templateConfig.getConfigContent());
			}else{
				printRequest.setTemplateVer(DmsConstants.TEMPLATE_VERSION_DEFAULT_EDN_PICKING);
			}
			JdResult<String> pdfResult = jdCloudPrintService.printPdfAndReturnWebUrl(printRequest);
			if(pdfResult != null && pdfResult.isSucceed()){
				printResult.setData(pdfResult.getData());
				//记录打印日志
				
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

	@Override
	public JdResponse<List<DmsEdnBatchVo>> printEdnDeliveryReceipt(String scheduleBillCode) {
		JdResponse<List<DmsEdnBatchVo>> printResult = new JdResponse<List<DmsEdnBatchVo>>();
		List<String> ednBatchNumList = this.queryEdnBatchNumList(scheduleBillCode);
		if(ednBatchNumList != null && ednBatchNumList.size() > 0){
			JdResult<List<DmsEdnBatchVo>> result = ednServiceManager.batchGetDeliveryReceipt(ednBatchNumList);
			if(result != null && result.isSucceed() && result.getData() != null){
				printResult.setData(result.getData());
				//记录打印日志
				
			}else if(result != null){
				printResult.toFail(result.getMessageCode()+ result.getMessage());
			}else{
				printResult.toFail("调用金鹏接口失败！");
			}
		}else{
			printResult.toFail("调度单下企配仓批次为空！");
		}
		return printResult;
	}
}
