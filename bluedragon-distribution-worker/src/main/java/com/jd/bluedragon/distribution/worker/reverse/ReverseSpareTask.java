package com.jd.bluedragon.distribution.worker.reverse;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.core.message.MessageDestinationConstant;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.qualityControl.domain.QualityControl;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.etms.basic.wss.BasicMajorWS;
import com.jd.etms.message.produce.client.MessageClient;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.wss.WaybillAddWS;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.api.request.ReverseSpareDto;
import com.jd.bluedragon.distribution.api.request.ReverseSpareRequest;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-11-28 上午10:28:50
 * 
 *             逆向备件库按商品退货分拣处理
 */
public class ReverseSpareTask extends DBSingleScheduler {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ReverseSpareService reverseSpareService;

	@Autowired
	private SendDatailDao sendDatailDao;

	@Autowired
	private MessageClient messageClient;

	@Autowired
	private WaybillAddWS waybillAddWS;

	@Autowired
	private BasicMajorWS basicMajorWS;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		String body = task.getBody();
		if (StringUtils.isBlank(body)) {
			return true;
		}

		ReverseSpareRequest[] requests = JsonHelper.jsonToArray(body,
				ReverseSpareRequest[].class);
		List<Sorting> sortings = new ArrayList<Sorting>();
		List<ReverseSpare> reverseSpares = new ArrayList<ReverseSpare>();
		for (ReverseSpareRequest request : requests) {
			Sorting sortingTemp = this.toSorting(request);
			if (sortingTemp == null) {
				this.logger
						.info("ReverseRejectSpareTask toDataMap--> sortingTemp对象为空，继续后面的");
				continue;
			}
			for (ReverseSpareDto spareDto : request.getData()) {
				ReverseSpare reverseSpare = this.toReverseSpare(spareDto);
				if (reverseSpare != null) {
					reverseSpare.setWaybillCode(request.getWaybillCode());
					Sorting sorting = (Sorting) sortingTemp.clone();
					sorting.setPackageCode(reverseSpare.getSpareCode());
					sortings.add(sorting);
					reverseSpares.add(reverseSpare);
				}
			}

			// 发质控和全程跟踪
			SendDetail sendDetail = new SendDetail();
			sendDetail.setBoxCode(request.getBoxCode());
			sendDetail.setWaybillCode(request.getWaybillCode());

			try {
				logger.warn("分拣中心异常节点备件库推全程跟踪、质控开始。运单号" + request.getWaybillCode());
				toQualityControlAndWaybillTrace(sendDetail, request);  // 推全程跟踪和质控
			} catch (Exception ex) {
				logger.error("分拣中心异常节点备件库推全程跟踪、质控发生异常。" + ex);
			}
		}

		if (!sortings.isEmpty()) {
			try {
				this.reverseSpareService.batchAddSorting(sortings,
						reverseSpares);
			} catch (Exception e) {
				this.logger.error("ReverseRejectSpareTask--> 服务处理异常：【" + body
						+ "】：", e);
				return false;
			}
		}
		return true;
	}


	public void toQualityControlAndWaybillTrace(SendDetail sendDetail, ReverseSpareRequest request){
		BdTraceDto bdTraceDto = convert2WaybillTrace(sendDetail, request);
		QualityControl qualityControl = convert2QualityControl(request);
		logger.warn("分拣中心备件库分拣发质控和全程跟踪开始。运单号 " + request.getWaybillCode());
		waybillAddWS.sendBdTrace(bdTraceDto);   // 推全程跟踪
		messageClient.sendMessage(MessageDestinationConstant.QualityControlMQ.getName(), JsonHelper.toJson(qualityControl), request.getBoxCode() != null ? request.getBoxCode() : request.getWaybillCode());   // 推质控
	}


	public BdTraceDto convert2WaybillTrace(SendDetail sendDetail, ReverseSpareRequest request){
		BdTraceDto bdTraceDto = new BdTraceDto();
		bdTraceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_QC);
		bdTraceDto.setOperatorSiteId(request.getSiteCode());
		bdTraceDto.setOperatorSiteName(request.getSiteName());
		bdTraceDto.setOperatorTime(DateHelper.parseDateTime(request.getOperateTime()));
		bdTraceDto.setOperatorUserId(request.getUserCode());
		bdTraceDto.setOperatorUserName(request.getUserName());
		bdTraceDto.setPackageBarCode(sendDetail.getPackageBarcode());
		bdTraceDto.setWaybillCode(sendDetail.getWaybillCode());
//		bdTraceDto.setOperatorDesp("包裹记录【" + request.getSpareReason() + "】异常");
		bdTraceDto.setOperatorDesp(request.getTrackContent());
		return bdTraceDto;
	}


	public QualityControl convert2QualityControl(ReverseSpareRequest request){
		QualityControl qualityControl = new QualityControl();
		qualityControl.setBlameDept(request.getSiteCode());
		qualityControl.setBlameDeptName(request.getSiteName());
		qualityControl.setCreateTime(DateHelper.parseDateTime(request.getOperateTime()));
		qualityControl.setCreateUserId(request.getUserCode());
		qualityControl.setCreateUserName(request.getUserName());
		qualityControl.setMessageType(QualityControl.QC_SPARE);
		if (request.getBoxCode() != null) {
			qualityControl.setBoxCode(request.getBoxCode());
		}else{
			qualityControl.setBoxCode("null");
		}
		qualityControl.setWaybillCode(request.getWaybillCode());
		qualityControl.setTypeCode(request.getSpareCode() + "");
		qualityControl.setExtraCode("null");
		qualityControl.setSystemName(QualityControl.SYSTEM_NAME);
		qualityControl.setReturnState("null");
		return qualityControl;
	}

	private Sorting toSorting(ReverseSpareRequest request) {
		String aBoxCode = request.getBoxCode();
		String aWaybillCode = request.getWaybillCode();
		if (StringUtils.isBlank(aBoxCode) || StringUtils.isBlank(aWaybillCode)) {
			this.logger
					.debug("ReverseRejectSpareTask toSorting--> 传入箱号或运单号为空");
			return null;
		}

		Sorting sorting = new Sorting();
		sorting.setCreateSiteCode(request.getSiteCode());
		sorting.setCreateSiteName(request.getSiteName());
		sorting.setReceiveSiteCode(request.getReceiveSiteCode());
		sorting.setReceiveSiteName(request.getReceiveSiteName());
		sorting.setCreateUser(request.getUserName());
		sorting.setCreateUserCode(request.getUserCode());
		sorting.setUpdateUser(request.getUserName());
		sorting.setUpdateUserCode(request.getUserCode());
		sorting.setIsCancel(request.getIsCancel());
		sorting.setType(request.getBusinessType());
		sorting.setOperateTime(DateHelper
				.getSeverTime(request.getOperateTime()));
		sorting
				.setCreateTime(DateHelper
						.getSeverTime(request.getOperateTime()));
		sorting.setBoxCode(aBoxCode);
		sorting.setWaybillCode(aWaybillCode);
		sorting.setSpareReason(request.getSpareReason());
		return sorting;
	}

	private ReverseSpare toReverseSpare(ReverseSpareDto spareDto) {
		if (spareDto == null || StringUtils.isBlank(spareDto.getSpareCode())) {
			this.logger
					.debug("ReverseRejectSpareTask toReverseSpare-->传入对象或备件条码为空");
			return null;
		}
		ReverseSpare reverseSpare = new ReverseSpare();
		reverseSpare.setSpareCode(spareDto.getSpareCode());
		reverseSpare.setProductId(spareDto.getProductId());
		reverseSpare.setProductCode(spareDto.getProductCode());
		reverseSpare.setProductName(spareDto.getProductName());
		reverseSpare.setProductPrice(spareDto.getProductPrice());
		reverseSpare.setArrtCode1(spareDto.getArrtCode1());
		reverseSpare.setArrtDesc1(spareDto.getArrtDesc1());
		reverseSpare.setArrtCode2(spareDto.getArrtCode2());
		reverseSpare.setArrtDesc2(spareDto.getArrtDesc2());
		reverseSpare.setArrtCode3(spareDto.getArrtCode3());
		reverseSpare.setArrtDesc3(spareDto.getArrtDesc3());
		reverseSpare.setArrtCode4(spareDto.getArrtCode4());
		reverseSpare.setArrtDesc4(spareDto.getArrtDesc4());
		return reverseSpare;
	}

}
