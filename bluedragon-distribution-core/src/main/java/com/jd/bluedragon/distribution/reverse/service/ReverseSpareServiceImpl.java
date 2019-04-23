package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.ReverseSpareDto;
import com.jd.bluedragon.distribution.api.request.ReverseSpareRequest;
import com.jd.bluedragon.distribution.qualityControl.domain.QualityControl;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.spare.dao.SpareSortingRecordDao;
import com.jd.bluedragon.distribution.spare.domain.SpareSortingRecord;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-11-28 下午05:21:37
 * 
 *             逆向备件库按商品退货分拣处理服务实现
 */
@Service("reverseSpareService")
public class ReverseSpareServiceImpl implements ReverseSpareService {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private SortingService sortingService;

	@Autowired
	private ReverseSpareDao reverseSpareDao;

    @Autowired
    @Qualifier("bdExceptionToQcMQ")
    private DefaultJMQProducer bdExceptionToQcMQ;

    @Autowired
    private WaybillTraceApi waybillTraceApi;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SpareSortingRecordDao spareSortingRecordDao;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int batchAddSorting(List<Sorting> sortings,
			List<ReverseSpare> reverseSpares) {
		if (sortings == null || sortings.size() <= 0) {
			this.logger
					.info("ReverseSpareServiceImpl batchAddSorting --> 传入参数不合法");
			return Constants.RESULT_FAIL;
		}
		 
		Collections.sort(sortings);
		this.sortingService.taskToSorting(sortings);

		this.batchAddOrUpdate(reverseSpares);

		return Constants.RESULT_SUCCESS;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int batchAddOrUpdate(List<ReverseSpare> reverseSpares) {
		if (reverseSpares == null || reverseSpares.size() <= 0) {
			this.logger
					.info("ReverseSpareServiceImpl batchAddOrUpdate --> 传入参数不合法");
			return Constants.RESULT_FAIL;
		}
		for (ReverseSpare reverseSpare : reverseSpares) {
			if (Constants.NO_MATCH_DATA == this.reverseSpareDao.update(
					ReverseSpareDao.namespace, reverseSpare).intValue()) {
				this.reverseSpareDao.add(ReverseSpareDao.namespace,
						reverseSpare);
			}
		}

		return Constants.RESULT_SUCCESS;
	}

	@Override
	public Sorting querySortingBySpareCode(Sorting sorting) {
		List<Sorting> sortings = this.sortingService.queryByCode(sorting);
		if (sortings != null && !sortings.isEmpty()) {
			return sortings.get(0);
		}
		return null;
	}

	@Override
	public ReverseSpare queryBySpareCode(String spwareCode) {
		return this.reverseSpareDao.queryBySpareCode(spwareCode);
	}
	
	@Override
	public List<ReverseSpare> queryByWayBillCode(String waybillCode,String sendCode) {
		ReverseSpare rs = new ReverseSpare();
		rs.setSendCode(sendCode);
		rs.setWaybillCode(waybillCode);
		return this.reverseSpareDao.queryByWayBillCode(rs);
	}
	
	@Override
	public List<ReverseSpare> queryBySpareTranCode(String spareTranCode) {
		return this.reverseSpareDao.queryBySpareTranCode(spareTranCode);
	}

    @Override
    public boolean doReverseSpareTask(Task task) throws Exception{
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

            //记录备件库分拣记录
            this.insertSpareSortingRecord(request);

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
                this.batchAddSorting(sortings,
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
        logger.warn("分拣中心备件库分拣发质控和全程跟踪开始，消息体：" + JsonHelper.toJson(qualityControl));
        waybillTraceApi.sendBdTrace(bdTraceDto);   // 推全程跟踪
        //messageClient.sendMessage(MessageDestinationConstant.QualityControlMQ.getName(), JsonHelper.toJson(qualityControl), request.getBoxCode() != null ? request.getBoxCode() : request.getWaybillCode());   // 推质控
        bdExceptionToQcMQ.sendOnFailPersistent(request.getBoxCode() != null ? request.getBoxCode() : request.getWaybillCode(), JsonHelper.toJson(qualityControl));

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
        try{
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(request.getUserCode());
            if(dto != null){
                String userErp = dto.getErp();//只有自营人员才有
                if(StringUtils.isEmpty(userErp)){
                    userErp = dto.getAccountNumber();
                }
                qualityControl.setCreateUserErp(userErp);
            }else{
                logger.warn("逆向备件库任务推质控查询用户为空,userCode："+request.getUserCode());
            }
        }catch (Exception e){
            logger.warn("逆向备件库任务推质控查询用户erp异常,userCode："+request.getUserCode()+"，异常原因："+e.getMessage());
        }
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

    /*
    * 保存备件库分拣记录
    * */
    private void insertSpareSortingRecord(ReverseSpareRequest reverseSpareRequest) {
        SpareSortingRecord spareSortingRecord = new SpareSortingRecord();
        try {
            //组装实体
            spareSortingRecord.setBoxCode(reverseSpareRequest.getBoxCode());
            spareSortingRecord.setWaybillCode(reverseSpareRequest.getWaybillCode());
            spareSortingRecord.setCreateSiteCode(reverseSpareRequest.getSiteCode());
            spareSortingRecord.setCreateSiteName(reverseSpareRequest.getSiteName());
            spareSortingRecord.setReceiveSiteCode(reverseSpareRequest.getReceiveSiteCode());
            spareSortingRecord.setReceiveSiteName(reverseSpareRequest.getReceiveSiteName());
            spareSortingRecord.setDutyCode(reverseSpareRequest.getDutyCode());
            spareSortingRecord.setDutyName(reverseSpareRequest.getDutyName());
            spareSortingRecord.setSpareReason(reverseSpareRequest.getSpareReason());
            spareSortingRecord.setCreateUser(reverseSpareRequest.getUserName());
            spareSortingRecord.setCreateUserCode(reverseSpareRequest.getUserCode());
            spareSortingRecord.setCreateTime(new Date());
            spareSortingRecordDao.insert(spareSortingRecord);
        } catch (Exception e) {
            logger.error("保存备件库分拣记录失败！" + JsonHelper.toJson(spareSortingRecord), e);
        }

    }
}
