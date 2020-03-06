package com.jd.bluedragon.distribution.reassignWaybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.domain.SiteChangeMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.command.JdResult;

import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.reassignWaybill.dao.ReassignWaybillDao;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.fastjson.JSONObject;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

@Service("reassignWaybill")
public class ReassignWaybillServiceImpl implements ReassignWaybillService {
	private static final Logger log = LoggerFactory.getLogger(ReassignWaybillServiceImpl.class);


	@Autowired
	private LogEngine logEngine;

	@Autowired
	private ReassignWaybillDao reassignWaybillDao;
	@Autowired
	private CenConfirmService cenConfirmService;
	@Autowired
	private DefaultJMQProducer reassignWaybillMQ;
	@Autowired
	@Qualifier("waybillSiteChangeProducer")
	private DefaultJMQProducer waybillSiteChangeProducer;


	private Boolean add(ReassignWaybill packTagPrint) {
		long startTime=new Date().getTime();

		Assert.notNull(packTagPrint, "packTagPrint must not be null");
		CenConfirm cenConfirm=new CenConfirm();
		cenConfirm.setWaybillCode(packTagPrint.getWaybillCode());
		cenConfirm.setCreateSiteCode(packTagPrint.getSiteCode());
		cenConfirm.setPackageBarcode(packTagPrint.getPackageBarcode());
		cenConfirm.setOperateType(Constants.OPERATE_TYPE_RCD);
		cenConfirm.setType(Integer.valueOf(Constants.BUSSINESS_TYPE_RCD).shortValue());
		cenConfirm.setOperateTime(packTagPrint.getOperateTime());
		cenConfirm.setOperateUser(packTagPrint.getUserName());
		cenConfirm.setOperateUserCode(packTagPrint.getUserCode());
		cenConfirm.setReceiveSiteCode(packTagPrint.getChangeSiteCode());
		cenConfirmService.syncWaybillStatusTask(cenConfirm);
		//添加返调度运单信息到本地库
		sendReassignWaybillMq(packTagPrint);

		if(WaybillUtil.getCurrentPackageNum(packTagPrint.getPackageBarcode()) == 1){
			//每个运单只需要发一次就可以
			SiteChangeMqDto siteChangeMqDto = new SiteChangeMqDto();
			siteChangeMqDto.setWaybillCode(packTagPrint.getWaybillCode());
			siteChangeMqDto.setPackageCode(packTagPrint.getPackageBarcode());
			siteChangeMqDto.setNewSiteId(packTagPrint.getChangeSiteCode());
			siteChangeMqDto.setNewSiteName(packTagPrint.getChangeSiteName());
			siteChangeMqDto.setNewSiteRoadCode("0"); // 此操作无法触发预分拣 故传默认值0
			siteChangeMqDto.setOperatorId(packTagPrint.getUserCode());
			siteChangeMqDto.setOperatorName(packTagPrint.getUserName());
			siteChangeMqDto.setOperatorSiteId(packTagPrint.getSiteCode());
			siteChangeMqDto.setOperatorSiteName(packTagPrint.getSiteName());
			siteChangeMqDto.setOperateTime(DateHelper.formatDateTime(new Date()));
			try {
				waybillSiteChangeProducer.sendOnFailPersistent(packTagPrint.getWaybillCode(), JsonHelper.toJsonUseGson(siteChangeMqDto));
				if(log.isDebugEnabled()){
					log.debug("发送预分拣站点变更mq消息成功(现场预分拣)：{}",JsonHelper.toJsonUseGson(siteChangeMqDto));
				}
			} catch (Exception e) {
				log.error("发送预分拣站点变更mq消息失败(现场预分拣)：{}",JsonHelper.toJsonUseGson(siteChangeMqDto), e);
			}finally{

				long endTime = new Date().getTime();

				JSONObject response=new JSONObject();
				response.put("keyword2", String.valueOf(siteChangeMqDto.getOperatorId()));
				response.put("keyword3", waybillSiteChangeProducer.getTopic());
				response.put("keyword4", siteChangeMqDto.getOperatorSiteId()==null?0:siteChangeMqDto.getOperatorSiteId().longValue());
				response.put("content", JsonHelper.toJsonUseGson(siteChangeMqDto));

				JSONObject request=new JSONObject();
				request.put("waybillCode",siteChangeMqDto.getWaybillCode());
				request.put("packageCode",siteChangeMqDto.getPackageCode());
				request.put("operatorName",siteChangeMqDto.getOperatorName());

				BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
						.operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SORTING_PRE_SITE_CHANGE)
						.methodName("ReassignWaybillServiceImpl#add")
						.operateRequest(request)
						.operateResponse(response)
						.build();
				businessLogProfiler.setProcessTime(endTime-startTime);

				logEngine.addLog(businessLogProfiler);

				SystemLogUtil.log(siteChangeMqDto.getWaybillCode(), String.valueOf(siteChangeMqDto.getOperatorId()), waybillSiteChangeProducer.getTopic(),
						siteChangeMqDto.getOperatorSiteId()==null?0:siteChangeMqDto.getOperatorSiteId().longValue(), JsonHelper.toJsonUseGson(siteChangeMqDto), SystemLogContants.TYPE_SITE_CHANGE_MQ_OF_OTHER);
			}

		}

		return reassignWaybillDao.add(packTagPrint);
	}

	/**
	 * 添加返调度运单信息到运单本地库
	 * 这里需要添加try catch 为了防止报错之后 后边的不能用
	 *
	 * 只有在分拣中心返调度的才会正常落数据 站点返调度不处理
	 * */
	private boolean sendReassignWaybillMq(ReassignWaybill packTagPrint){
		try {
			String json = JsonHelper.toJsonUseGson(packTagPrint);
			reassignWaybillMQ.sendOnFailPersistent(packTagPrint.getWaybillCode(), json);
		}catch (Exception e){
			return false;
		}
		return true;
	}

    @Override
    @JProfiler(jKey= "DMSWEB.ReassignWaybillService.packLastScheduleSite")
    public ReassignWaybill queryByPackageCode(String packageCode) {
        Assert.notNull(packageCode, "packageCode must not be null");
        return reassignWaybillDao.queryByPackageCode(packageCode);
    }

	@Override
	public ReassignWaybill queryByWaybillCode(String waybillCode) {
		 Assert.notNull(waybillCode, "waybillCode must not be null");
	        return reassignWaybillDao.queryByWaybillCode(waybillCode);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public JdResult<Boolean> backScheduleAfter(ReassignWaybillRequest reassignWaybillRequest) {
    	JdResult<Boolean> jdResult = new JdResult<Boolean>();
		jdResult.setData(Boolean.FALSE);
		jdResult.toFail();
		if (reassignWaybillRequest == null || StringUtils.isBlank(reassignWaybillRequest.getPackageBarcode())) {
			log.warn("backScheduleAfter --> 传入参数非法");
			jdResult.toFail(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			return jdResult;
		}
		log.info("backScheduleAfter--> packageBarcode is [{}]",reassignWaybillRequest.getPackageBarcode());
		ReassignWaybill packTagPrint = ReassignWaybill.toReassignWaybill(reassignWaybillRequest);
		if (add(packTagPrint)) {
			jdResult.toSuccess();
			jdResult.setData(Boolean.TRUE);
		} else {
			jdResult.toFail(308, "处理失败");
		}
		return jdResult;
	}
}
