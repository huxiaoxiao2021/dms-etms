package com.jd.bluedragon.distribution.sendprint.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.quickProduce.domain.JoinDetail;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.dao.SendMReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sendprint.domain.BasicQueryEntity;
import com.jd.bluedragon.distribution.sendprint.domain.BasicQueryEntityResponse;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendInfoResponse;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendResult;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintBoxEntity;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResult;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

@Service
public class SendPrintServiceImpl implements SendPrintService{
	
	@Autowired
	private SendMDao sendMDao;
	
	@Autowired
	private SendMReadDao sendMReadDao;
	
	@Autowired
	private SendDatailDao sendDatailDao;
	
	@Autowired
	WaybillQueryApi waybillQueryApi;

    @Autowired
    WaybillPackageApi waybillPackageApi;
	
	@Autowired
	private WaybillPickupTaskApi waybillPickupTaskApi;
	
	@Autowired
    private SealBoxService tSealBoxService;
	
	@Autowired
    private BaseMajorManager baseMajorManager;
	
	private final Logger logger = Logger.getLogger(SendPrintServiceImpl.class);
	
	@Autowired
    private QuickProduceService quickProduceService;

    @Autowired
    private BoxService boxService;

	/**
	 * 
	 * 
	 * 批次汇总&&批次汇总打印
	 */
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @JProfiler(jKey = "DMSWEB.SendPrintServiceImpl.batchSummaryPrintQuery", mState = {JProEnum.TP, JProEnum.FunctionError})
	public SummaryPrintResultResponse batchSummaryPrintQuery(PrintQueryCriteria criteria) {
	    SummaryPrintResultResponse tSummaryPrintResultResponse = new SummaryPrintResultResponse();
		List<SummaryPrintResult> results = new ArrayList<SummaryPrintResult>();
		SendM nSendM = tosendM(criteria);
		Date startDate = new Date();
		logger.info("打印交接清单-批次汇总开始"+DateHelper.formatDate(startDate));
		//满足条件的所有箱号
		List<SendM> sendMs =this.selectUniquesSendMs(nSendM); //this.sendMDao.selectBySendSiteCode(nSendM);
		if (sendMs != null && !sendMs.isEmpty()) {
			logger.info("打印交接清单-批次汇总数目"+sendMs.size());
		    Set<SendM> sendmset = new CollectionHelper<SendM>().toSet(sendMs);
            try {
                for (SendM sendM : sendmset) {
                    SummaryPrintResult result = summaryPrintQuery(sendM,sendMs,criteria);
                    if(result!=null){
                        results.add(result);
                    }
                }
            } catch (Exception e) {
                logger.error("批次汇总&&批次汇总打印异常", e);
                tSummaryPrintResultResponse.setCode(JdResponse.CODE_NOT_FOUND);
                tSummaryPrintResultResponse.setMessage("批次汇总打印异常");
                tSummaryPrintResultResponse.setData(results);
                return tSummaryPrintResultResponse;
            }
		}
		tSummaryPrintResultResponse.setCode(JdResponse.CODE_OK);
		tSummaryPrintResultResponse.setMessage(JdResponse.MESSAGE_OK);
		tSummaryPrintResultResponse.setData(results);
		Date endDate = new Date();
		logger.info("打印交接清单-批次汇总结束-"+(startDate.getTime() - endDate.getTime()));
		return tSummaryPrintResultResponse;
	}

	/**
	 * 
	 * 
     * 解析参数
     */
    private SendM tosendM(PrintQueryCriteria criteria) {
    	
        SendM nSendM = new SendM();
		nSendM.setCreateSiteCode(criteria.getSiteCode());
		nSendM.setReceiveSiteCode(criteria.getReceiveSiteCode());
		
		if(criteria.getSendCode()!=null && !"".equals(criteria.getSendCode())){
            nSendM.setSendCode(criteria.getSendCode());
        }
		if(criteria.getBoxCode()!=null && !"".equals(criteria.getBoxCode())){
		    nSendM.setBoxCode(criteria.getBoxCode());
        }else{
        	Date startTime = DateHelper.parseDateTime(criteria.getStartTime());
    		nSendM.setOperateTime(startTime);
    		Date endTime = DateHelper.parseDateTime(criteria.getEndTime());
    		nSendM.setUpdateTime(endTime);
        }
		if(criteria.getSendUserCode()!=null){
		    nSendM.setCreateUserCode(criteria.getSendUserCode());
        }
        return nSendM;
    }

	/**
	 * 
	 * 
	 * 汇总&&汇总打印
	 */
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public SummaryPrintResult summaryPrintQuery(SendM sendM,List<SendM> sendMs,PrintQueryCriteria criteria) {
		Date startDate = new Date();
		logger.info("打印交接清单-summaryPrintQuery开始"+DateHelper.formatDate(startDate));
		SummaryPrintResult result = new SummaryPrintResult();
		List<SummaryPrintBoxEntity> details = new ArrayList<SummaryPrintBoxEntity>();
		result.setSendCode(sendM.getSendCode());
		result.setReceiveSiteName(toSiteName(criteria.getReceiveSiteCode()));
		result.setSendSiteName(toSiteName(criteria.getSiteCode()));
		result.setSendTime(DateHelper.formatDateTime(sendM.getOperateTime()));
		List<SendM> sendMList = new ArrayList<SendM>();
		for (SendM dendM : sendMs) {
		    //当前批次的所有箱号
		    if(sendM.getSendCode().equals(dendM.getSendCode())){
		        sendMList.add(dendM);
		    }
		}
		logger.info("打印交接清单-批次汇总箱子数量"+sendMList.size());
		for (SendM dendM : sendMList) {
			Date startDate1 = new Date();
			logger.info("打印交接清单-批次单独批次开始"+DateHelper.formatDate(startDate1));
		    Set<String> packageBarcodeSet = new HashSet<String>();
		    Set<String> waybillCodeSet = new HashSet<String>();
		    SendDetail tSendDatail = new SendDetail();
		    tSendDatail.setCreateSiteCode(dendM.getCreateSiteCode());
		    tSendDatail.setBoxCode(dendM.getBoxCode());
		    tSendDatail.setReceiveSiteCode(dendM.getReceiveSiteCode());
		    tSendDatail.setIsCancel(0);
		    logger.info("打印交接清单-批次汇总箱子信息"+dendM.getBoxCode());
		    List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);
//		    if(sendDetails!=null && !sendDetails.isEmpty()){ 使打印交接汇总清单时带出空箱，之前不打印空箱
		    for (SendDetail sendDatail : sendDetails) {
	            String packageBarcode = sendDatail.getPackageBarcode();
	            if(criteria.getPackageBarcode()!=null && !"".equals(criteria.getPackageBarcode()) &&
	            		!criteria.getPackageBarcode().equals(packageBarcode)){
	                	continue;
	            }
	            String waybillCode = sendDatail.getWaybillCode();
	            packageBarcodeSet.add(packageBarcode);
	            waybillCodeSet.add(waybillCode);
	        }
		    SummaryPrintBoxEntity detail = new SummaryPrintBoxEntity();
		    detail.setBoxCode(dendM.getBoxCode());
            detail.setPackageBarNum(packageBarcodeSet.size());
            detail.setPackageBarRecNum(packageBarcodeSet.size());
            detail.setWaybillNum(waybillCodeSet.size());
            Double boxOrPackVolume = 0.0;
            if(BusinessHelper.isBoxcode(dendM.getBoxCode())){
                Box box = null;
                try {
                    box = boxService.findBoxByCode(dendM.getBoxCode());
                } catch (Exception e) {
                    logger.error("打印交接清单获取箱号失败", e);
                    logger.error(JsonHelper.toJson(dendM));
                }
                if(null != box && null != box.getLength() && null != box.getWidth() && null != box.getHeight()
                        && box.getLength() > 0 && box.getWidth() > 0 && box.getHeight() > 0) {
                    boxOrPackVolume = Double.valueOf(box.getLength() * box.getWidth() * box.getHeight());
                }
            	SealBox tSealBox = this.tSealBoxService.findByBoxCode(dendM.getBoxCode());
                if(tSealBox!=null){
                    detail.setSealNo1(tSealBox.getCode());
                    detail.setSealNo2("");
                    detail.setLockTime(DateHelper.formatDateTime(tSealBox.getCreateTime()));
                }else{
                    detail.setSealNo1("");
                    detail.setSealNo2("");
                }
            }else{
                if(BusinessHelper.isPackageCode(dendM.getBoxCode())) {
                    PackOpeFlowDto packOpeFlowDto = getOpeByPackageCode(dendM.getBoxCode());
                    if(null != packOpeFlowDto && null != packOpeFlowDto.getpLength() && null != packOpeFlowDto.getpWidth() && null != packOpeFlowDto.getpHigh()
                             && packOpeFlowDto.getpLength() > 0 && packOpeFlowDto.getpWidth() > 0 && packOpeFlowDto.getpHigh() > 0) {
                        boxOrPackVolume = packOpeFlowDto.getpLength() * packOpeFlowDto.getpWidth() * packOpeFlowDto.getpHigh();
                    }
                }
                detail.setSealNo1("");
                detail.setSealNo2("");
            }
            detail.setVolume(boxOrPackVolume);
            details.add(detail);
//		    }
		    Date endDate1 = new Date();
			logger.info("打印交接清单-批次单独批次结束-"+(startDate1.getTime() - endDate1.getTime()));
        }
		result.setTotalBoxNum(sendMList.size());
		result.setDetails(details);
		Date endDate = new Date();
		logger.info("打印交接清单-summaryPrintQuery结束-"+(startDate.getTime() - endDate.getTime()));
		return result;
	}

    private PackOpeFlowDto getOpeByPackageCode(String packageCode) {
        try{
            String waybillCode = BusinessHelper.getWaybillCode(packageCode);
            BaseEntity<List<PackOpeFlowDto>> packageOpe = waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
            for(PackOpeFlowDto packOpeFlowDto : packageOpe.getData()) {
                if(packOpeFlowDto.getPackageCode().equals(packageCode)) {
                    return packOpeFlowDto;
                }
            }
        } catch (Exception e) {
            logger.error("获取包裹量方信息接口失败，原因", e);
        }
        return null;
    }

	/**
	 * 明细打印
	 */
	public BasicQueryEntityResponse detailPrintQuery(List<SendM> sendMs,PrintQueryCriteria criteria) {
		Date startDate = new Date();
	    logger.info("打印交接清单-detailPrintQuery开始"+DateHelper.formatDate(startDate));
		
		BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
	    List<BasicQueryEntity> fzList = new ArrayList<BasicQueryEntity>();
	    String rsiteName = toSiteName(criteria.getReceiveSiteCode());
	    String fsiteName = toSiteName(criteria.getSiteCode());
	    Integer rSiteType = toSiteType(criteria.getReceiveSiteCode());
	    
	    String message = JdResponse.MESSAGE_OK;
	    for (SendM dendM : sendMs) {
	    	List<BasicQueryEntity> tList = new ArrayList<BasicQueryEntity>();
	    	List<BasicQueryEntity> mList = new ArrayList<BasicQueryEntity>();
	    	String sealNo = "";
	        SendDetail tSendDatail = new SendDetail();
            tSendDatail.setCreateSiteCode(dendM.getCreateSiteCode());
            tSendDatail.setBoxCode(dendM.getBoxCode());
            tSendDatail.setReceiveSiteCode(dendM.getReceiveSiteCode());
            tSendDatail.setIsCancel(1);
            
            if(BusinessHelper.isBoxcode(dendM.getBoxCode())){
            	SealBox tSealBox = this.tSealBoxService.findByBoxCode(dendM.getBoxCode());
                if(tSealBox!=null){
                	sealNo = tSealBox.getCode();
                }
            }
            
	        List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);
	        
	        if (sendDetails != null && !sendDetails.isEmpty()) {
	            List<String> waybillCodes = new ArrayList<String>();
	            try {
                    for (SendDetail dSendDatail : sendDetails) {
                        if(criteria.getPackageBarcode()==null || "".equals(criteria.getPackageBarcode()) 
                        		|| criteria.getPackageBarcode().equals(dSendDatail.getPackageBarcode())){
                        	if(dSendDatail.getWaybillCode()!=null && !dSendDatail.getWaybillCode().isEmpty()){
                                waybillCodes.add(dSendDatail.getWaybillCode());
                            }
                        	BasicQueryEntity tBasicQueryEntity = new BasicQueryEntity();
                            tBasicQueryEntity.setBoxCode(dendM.getBoxCode());
                            if(dSendDatail.getIsCancel()!=null && dSendDatail.getIsCancel()==0){
                                tBasicQueryEntity.setIscancel("否");
                            }else{
                                tBasicQueryEntity.setIscancel("是");
                            }
                            tBasicQueryEntity.setSealNo(sealNo);
                            tBasicQueryEntity.setIsnew("否");
                            tBasicQueryEntity.setPackageBarWeight(0.0);
                            tBasicQueryEntity.setPackageBarWeight2(0.0);
                            tBasicQueryEntity.setPackageBar(dSendDatail.getPackageBarcode());
                            tBasicQueryEntity.setReceiveSiteCode(dendM.getReceiveSiteCode());
                            tBasicQueryEntity.setReceiveSiteName(rsiteName);
                            tBasicQueryEntity.setSendCode(dendM.getSendCode());
                            tBasicQueryEntity.setReceiveSiteType(rSiteType);
                            tBasicQueryEntity.setSendSiteName(fsiteName);
                            tBasicQueryEntity.setSendUser(dendM.getCreateUser());
                            tBasicQueryEntity.setSendUserCode(dendM.getCreateUserCode());
                            tBasicQueryEntity.setWaybill(dSendDatail.getWaybillCode());
                            tBasicQueryEntity.setInvoice(dSendDatail.getPickupCode());
                            tList.add(tBasicQueryEntity);
                        }
                    }
                    HashMap<String, BigWaybillDto> deliveryPackageMap = new HashMap<String, BigWaybillDto>();
	            	if(waybillCodes!=null && !waybillCodes.isEmpty()){
	            		int n = waybillCodes.size()/50;
		            	int m = waybillCodes.size()%50;
	            		if(n > 0){
	            			List<String> waybills = new ArrayList<String>();
	            			int num =0;
	            			for(String code :waybillCodes){
			            		num++;
			            		waybills.add(code);
			            		if(num/50>0 && num%50==0 && n>0){
			            			sendToWaybill(deliveryPackageMap,waybills);
			            			waybills = new ArrayList<String>();
			            			n--;
			            		}else if(n==0 && m>0 && waybillCodes.size()==num){
			            			sendToWaybill(deliveryPackageMap,waybills);
			            		}
			            	}
	            		}else{
	            			sendToWaybill(deliveryPackageMap,waybillCodes);
	            		}
	            	}

                	for(BasicQueryEntity dBasicQueryEntity :tList){
                		if(rSiteType!=null && rSiteType.equals(Integer.parseInt(PropertiesHelper.newInstance().getValue("asm_type")))){
                    		Date startDate2 = new Date();
        	    			logger.info("打印交接清单-调用取件单接口开始"+DateHelper.formatDate(startDate2));
        	    			if(dBasicQueryEntity.getInvoice()!=null){
        	    				BaseEntity<PickupTask> tPickupTask =waybillPickupTaskApi.getPickTaskByPickCode(dBasicQueryEntity.getInvoice());
                                if(tPickupTask!=null && tPickupTask.getResultCode()>0){
                                    PickupTask mPickupTask = tPickupTask.getData();
                                    if(mPickupTask!=null){
                                    	dBasicQueryEntity.setInvoice(mPickupTask.getInvoiceId());
                                    	if(mPickupTask.getNewWaybillCode()!=null)
                                    	dBasicQueryEntity.setIsnew("是");
                                    }
                                }else{
                                	message="取件单基础信息调用异常"+dBasicQueryEntity.getWaybill();
                                    logger.error("取件单基础信息调用异常"+dBasicQueryEntity.getWaybill());
                                }
        	    			}
        	    			Date endDate2 = new Date();
        	    			logger.info("打印交接清单-调用取件单接口结束-"+(startDate2.getTime() - endDate2.getTime()));
                        }
                		
                		if(dBasicQueryEntity.getWaybill()==null || dBasicQueryEntity.getWaybill().isEmpty()){
                			
                			logger.info("打印交接清单-如果运单号为空直接加入list返回");
                			continue;
                        }
                		
                    	BigWaybillDto data = deliveryPackageMap.get(dBasicQueryEntity.getWaybill());
                    	if(data==null || data.getWaybill() ==null){
                             continue;
         				}
                    	Waybill waybill = data.getWaybill();
                    	WaybillManageDomain waybillState = data.getWaybillState();
                    	List<DeliveryPackageD> deliveryPackage = data.getPackageList();
 						String declaredValue = (waybill == null) ? null : waybill.getCodMoney();   
 						Integer storeId=0;
 						if(waybillState != null && waybillState.getStoreId()!=null)
 						storeId = waybillState.getStoreId();
 						String sendPay = (waybill == null ? null : waybill.getSendPay());
 						Integer siteId = 0;
 						if(waybill != null && waybill.getOldSiteId()!=null)
 						siteId = waybill.getOldSiteId();
 						String siteName = null;
 		    			BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(siteId);
 	        			if(bDto!=null){ 
  				           siteName = bDto.getSiteName();
  				           Integer siteType = bDto.getSiteType();
  				           if(siteType!=null && !siteType.equals(16)){
  				        	  dBasicQueryEntity.setSiteType("自营");
  				           }
  				        }
 	        			dBasicQueryEntity.setDeclaredValue(declaredValue);
 	        			dBasicQueryEntity.setGoodValue((waybill == null || waybill.getPrice()==null)? "0.00": waybill.getPrice());
 	        			if(deliveryPackage!=null && !deliveryPackage.isEmpty()
 	        					&& BusinessHelper.checkIntNumRange(deliveryPackage.size())){
 	        				for(DeliveryPackageD delivery : deliveryPackage){
 	        					if(delivery.getPackageBarcode().equals(dBasicQueryEntity.getPackageBar())){
                                    dBasicQueryEntity.setGoodVolume(0.0);
                                    PackOpeFlowDto packOpeFlowDto = getOpeByPackageCode(delivery.getPackageBarcode());
                                    if(null != packOpeFlowDto && null != packOpeFlowDto.getpLength() && null != packOpeFlowDto.getpWidth() && null != packOpeFlowDto.getpHigh()
                                            && packOpeFlowDto.getpLength() > 0 && packOpeFlowDto.getpWidth() > 0 && packOpeFlowDto.getpHigh() > 0) {
                                        dBasicQueryEntity.setGoodVolume(packOpeFlowDto.getpLength() * packOpeFlowDto.getpWidth() * packOpeFlowDto.getpHigh());
                                    }
 	        						dBasicQueryEntity.setPackageBarWeight(delivery.getGoodWeight());
 	        						dBasicQueryEntity.setPackageBarWeight2(delivery.getAgainWeight());
 	        					}
 	        				}
 	        			}
 	        			dBasicQueryEntity.setFcNo(storeId);
                        //dBasicQueryEntity.setGoodVolume(0.0);
                        //if(waybill != null && waybill.getGoodVolume() != null)
                            //dBasicQueryEntity.setGoodVolume(waybill.getGoodVolume());
                        dBasicQueryEntity.setGoodWeight(0.0);
 	        			if(waybill != null && waybill.getGoodWeight()!=null)
  				        dBasicQueryEntity.setGoodWeight(waybill.getGoodWeight());
 	        			dBasicQueryEntity.setGoodWeight2(0.0);
 	        			if(waybill != null && waybill.getAgainWeight()!=null)
                        dBasicQueryEntity.setGoodWeight2(waybill.getAgainWeight());
 	        			dBasicQueryEntity.setPackageBarNum(0);
 	        			if(waybill != null && waybill.getGoodNumber()!=null)
                        dBasicQueryEntity.setPackageBarNum(waybill.getGoodNumber());
 	        			dBasicQueryEntity.setPayment(0);
 	        			if(waybill != null && waybill.getPayment()!=null)
                        dBasicQueryEntity.setPayment(waybill.getPayment());
                        
                        if(waybill!=null && rSiteType.equals(16)){
                        	dBasicQueryEntity.setReceiverAddress(waybill.getReceiverAddress() == null ? "": waybill.getReceiverAddress());
                        	String receiverMobile = waybill.getReceiverMobile()==null? "" : waybill.getReceiverMobile();
                        	String receiverTel = waybill.getReceiverTel()==null? "" : waybill.getReceiverTel();
                        	if(waybill.getReceiverMobile()==null && waybill.getReceiverTel()==null){
                        		dBasicQueryEntity.setReceiverMobile("--");
                        	}else{
                        		//dBasicQueryEntity.setReceiverMobile(receiverMobile+"/"+receiverTel);
                        		dBasicQueryEntity.setReceiverMobile("");
                        	}
                        }else{
                        	dBasicQueryEntity.setReceiverMobile("--");
                        }
                        dBasicQueryEntity.setReceiverName(waybill == null ? null: waybill.getReceiverName());
                        if(waybill == null || waybill.getPayment()==null){
                        	dBasicQueryEntity.setSendPay("");
                        }else{
                            dBasicQueryEntity.setSendPay(getSendPay(waybill.getPayment()));
                        	if(waybill.getPayment()!=1 && waybill.getPayment()!=3){
                        		dBasicQueryEntity.setDeclaredValue("0");
                        	}
                        }
                        //是否是奢侈品
                        if (sendPay != null && sendPay.charAt(19)=='1'){
                        	dBasicQueryEntity.setLuxury("是");
                        }else{
                        	dBasicQueryEntity.setLuxury("否");
                        }
                        dBasicQueryEntity.setSiteCode(siteId);
                        dBasicQueryEntity.setSiteName(siteName);
                        if(waybill==null || waybill.getWaybillType()==null){
                            dBasicQueryEntity.setWaybillType("一般订单");
                        }else{
                            dBasicQueryEntity.setWaybillType(getWaybillType(waybill.getWaybillType()));
                        }
                    	
                    	if (criteria.getFc()!= null && !criteria.getFc().equals(0) && storeId != null && 
                    		!criteria.getFc().equals(storeId)) {
                    		mList.add(dBasicQueryEntity);
 						}
                        if(criteria.isIs211() &&
                         	(sendPay != null && !"1".equals(sendPay.substring(0, 1)))){
                        	mList.add(dBasicQueryEntity);
                        }
                    }
                	if(mList!=null && !mList.isEmpty()){
                		for(BasicQueryEntity dBasicQueryEntity :mList){
                			tList.remove(dBasicQueryEntity);
                		}
                	}
                } catch (Exception e) {
                	message="同步运单基本信息异常错误原因为"+e.getMessage();
                    logger.error("同步运单基本信息异常错误原因为"+e.getMessage());
                }
	        }
	        if(tList!=null && !tList.isEmpty()){
		        for(BasicQueryEntity tBasicQueryEntity :tList){
		        	fzList.add(tBasicQueryEntity);
		        }
	        }
	     }
	    tBasicQueryEntityResponse.setCode(JdResponse.CODE_OK);
        tBasicQueryEntityResponse.setMessage(message);
	    tBasicQueryEntityResponse.setData(fzList); 
	    Date endDate = new Date();
		logger.info("打印交接清单-detailPrintQuery结束-"+(startDate.getTime() - endDate.getTime()));
		return tBasicQueryEntityResponse;
	}
	private String getWaybillType(int waybillType){
	      if(waybillType==0){
	          return "一般订单";
	      }else if(waybillType==127){
	          return "奢侈品";
	      }else if(waybillType==6){
	          return "分期付款";
	      }else if(waybillType==17){
	          return "在线分期";
	      }else if(waybillType==2){
	          return "拍卖订单";
	      }else if(waybillType==4){
	          return "虚拟产品";
	      }else if(waybillType==7){
	          return "内部订单";
	      }else if(waybillType==8){
	          return "服务产品订单";
	      }else if(waybillType==9){
	          return "备件库-行政";
	      }else if(waybillType==10){
	          return "备件库-售后";
	      }else if(waybillType==11){
              return "售后调货";
          }else if(waybillType==12){
              return "家电下乡";
          }else if(waybillType==13){
              return "企销部订单";
          }else if(waybillType==15){
              return "返修订单";
          }else if(waybillType==16){
              return "直接赔偿";
          }else if(waybillType==18){
              return "厂商直送";
          }else if(waybillType==19){
              return "客服补件";
          }else if(waybillType==21||waybillType==22||waybillType==23||waybillType==24||waybillType==25){
              return "POP订单";
          }else if(waybillType==20){
              return "以旧换新";
          }else if(waybillType==28||waybillType==29){
              return "团购订单";
          }else if(waybillType==10000){
              return "B商家订单";
          }
	      return "一般订单";
	    }
	private String getSendPay(int payment){
	  if(payment==1){
	      return "货到付款";
	  }else if(payment==2){
          return "邮局汇款";
      }else if(payment==3){
          return "上门自提";
      }else if(payment==4){
          return "在线支付";
      }else if(payment==5){
          return "公司转帐";
      }else if(payment==6){
          return "银行卡转帐";
      }else if(payment==8){
          return "分期付款";
      }else if(payment==10){
          return "高校代理-自己支付";
      }else if(payment==11){
          return "高校代理-代理垫付";
      }else if(payment==12){
          return "月结";
      }
	  return "";
	}
	@Override
	/**
	 * 基本查询
	 */
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @JProfiler(jKey = "DMSWEB.SendPrintServiceImpl.basicPrintQuery", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BasicQueryEntityResponse basicPrintQuery(PrintQueryCriteria criteria) {
		Date startDate = new Date();
	    logger.info("打印交接清单-基本信息查询开始"+DateHelper.formatDate(startDate));
	    BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
	    try {
		    SendM qSendM = tosendM(criteria);
	        List<SendM> sendMs =this.selectUniquesSendMs(qSendM);// this.sendMDao.selectBySendSiteCode(qSendM);
	        if(sendMs!=null && !sendMs.isEmpty()){
	        	tBasicQueryEntityResponse = detailPrintQuery(sendMs,criteria);
	        }
	    } catch (Exception e) {
            logger.error("打印明细基本查询异常");
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        }
		Date endDate = new Date();
		logger.info("打印交接清单-基本信息查询结束-"+(startDate.getTime() - endDate.getTime()));
		return tBasicQueryEntityResponse;
	}

    private String toSiteName(Integer siteCode) {
        BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        if(bDto==null){
        	return null;
        }
        return bDto.getSiteName();
    }
    
    private Integer toSiteType(Integer siteCode) {
        BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        if(bDto==null){
        	return null;
        }
        return bDto.getSiteType();
    }
    
    private HashMap<String, BigWaybillDto> sendToWaybill(HashMap<String, BigWaybillDto> deliveryPackageMap,List<String> waybillCodes){
    	
    	WChoice queryWChoice = new WChoice();
    	queryWChoice.setQueryWaybillC(true);
    	queryWChoice.setQueryWaybillE(true);
    	queryWChoice.setQueryWaybillM(true);
    	queryWChoice.setQueryPackList(true);
    	
    	Date startDate1 = new Date();
		logger.info("打印交接清单-调用运单接口开始"+DateHelper.formatDate(startDate1));
		BaseEntity<List<BigWaybillDto>> results = this.waybillQueryApi.getDatasByChoice(waybillCodes, queryWChoice);
        Date endDate1 = new Date();
		logger.info("打印交接清单-调用运单接口结束-"+(startDate1.getTime() - endDate1.getTime()));
        if(results!=null && results.getResultCode()>0){
        	logger.info("打印交接清单-调用运单接口返回信息"+results.getResultCode()+"-----"+results.getMessage());
        	List<BigWaybillDto> datas = results.getData();
        	if(datas!=null && !datas.isEmpty()){
        		for(BigWaybillDto data : datas){
                	if(data.getWaybill()!=null){
                		deliveryPackageMap.put(data.getWaybill().getWaybillCode(), data);
                	}
                }
        	}
        }
        return deliveryPackageMap;
    }

	@Override
	public BasicQueryEntityResponse sopPrintQuery(PrintQueryCriteria criteria) {
	    BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
	    try {
		    SendM qSendM = tosendM(criteria);
	        List<SendM> sendMs = this.sendMDao.selectBySendSiteCode(qSendM);
	        if(sendMs!=null && !sendMs.isEmpty()){
	        	tBasicQueryEntityResponse = detailPrintQuerySop(sendMs,criteria);	            
	        }
	    } catch (Exception e) {
            logger.error("打印明细基本查询异常");
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        }
		return tBasicQueryEntityResponse;
	}




    /**
     * 获取SendM 列表【去重后结果，按批次号及箱号去重】
     * @param domain 查询参数
     * @return
     */
    private final List<SendM> selectUniquesSendMs(SendM domain){
        List<SendM> sendMs = this.sendMDao.selectBySendSiteCode(domain);
        Set<String> set=new HashSet<String>();
        List<SendM> hasMap=new ArrayList<SendM>();
        if(null!=sendMs){
            for (SendM item :sendMs){
                if(!set.contains(item.getSendCode()+"-"+item.getBoxCode())){
                    set.add(item.getSendCode()+"-"+item.getBoxCode());
                    hasMap.add(item);
                }
            }
        }

        return hasMap;
    }

    /**
	 * 明细打印
	 */
	public BasicQueryEntityResponse detailPrintQuerySop(List<SendM> sendMs,PrintQueryCriteria criteria) {
	    logger.info("SOP打印交接清单-detailPrintQuerySop开始");
		BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
	    List<BasicQueryEntity> fzList = new ArrayList<BasicQueryEntity>();
	    String rsiteName = toSiteName(criteria.getReceiveSiteCode());
	    String fsiteName = toSiteName(criteria.getSiteCode());
	    
	    String message = JdResponse.MESSAGE_OK;
	    for (SendM dendM : sendMs) {
	    	List<BasicQueryEntity> tList = new ArrayList<BasicQueryEntity>();
	        SendDetail tSendDatail = new SendDetail();
            tSendDatail.setCreateSiteCode(dendM.getCreateSiteCode());
            tSendDatail.setBoxCode(dendM.getBoxCode());
            tSendDatail.setReceiveSiteCode(dendM.getReceiveSiteCode());
            tSendDatail.setIsCancel(1);
	        List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);
	        
	        if (sendDetails != null && !sendDetails.isEmpty()) {
	        	for (SendDetail dSendDatail : sendDetails) {
                    if(criteria.getWaybillcode()==null || "".equals(criteria.getWaybillcode()) 
                    		|| criteria.getWaybillcode().equals(dSendDatail.getWaybillCode())){
                    	BasicQueryEntity tBasicQueryEntity = new BasicQueryEntity();
                        tBasicQueryEntity.setPackageBar(dSendDatail.getPackageBarcode());
                        tBasicQueryEntity.setReceiveSiteName(rsiteName);
                        tBasicQueryEntity.setSendSiteName(fsiteName);
                        tBasicQueryEntity.setSendUser(dendM.getCreateUser());
                        tBasicQueryEntity.setPackageBarNum(dSendDatail.getPackageNum());
                        tBasicQueryEntity.setWaybill(dSendDatail.getWaybillCode());
                        tBasicQueryEntity.setOperateTime(DateHelper.formatDateTime(dendM.getOperateTime()));
                        tList.add(tBasicQueryEntity);
                    }
                }
	        }
	        if(tList!=null && !tList.isEmpty()){
		        for(BasicQueryEntity tBasicQueryEntity :tList){
		        	fzList.add(tBasicQueryEntity);
		        }
	        }
	    }
	    tBasicQueryEntityResponse.setCode(JdResponse.CODE_OK);
        tBasicQueryEntityResponse.setMessage(message);
	    tBasicQueryEntityResponse.setData(fzList); 
		return tBasicQueryEntityResponse;
	}


	@Override
	public BatchSendInfoResponse selectBoxBySendCode(List<BatchSend> batchSends) {
		logger.info("获取发货批次下的原包及箱子信息-selectBoxBySendCode");
        BatchSendInfoResponse batchSendInfoResponse = new BatchSendInfoResponse();
        try {
            Map<String, String> boxes = new HashMap<String, String>();// 存放箱号用于计算箱子数量
            Map<String, String> packages = new HashMap<String, String>();// 存放包裹号用于计算包裹数量
            for (int i = 0; i < batchSends.size(); i++) {
                List<String> scanCodeList = this.sendMReadDao.selectBoxCodeBySendCode(batchSends.get(i).getSendCode());
                if (scanCodeList != null && scanCodeList.size() > 0) {
                    for (String scanCode : scanCodeList) {
                        if (BusinessHelper.isBoxcode(scanCode))
                            boxes.put(scanCode, scanCode);
                        else if (BusinessHelper.isPackageCode(scanCode))
                            packages.put(scanCode, scanCode);
                    }
                }
            }

            // 组装返回值对象
            BatchSendResult batchSendResult = new BatchSendResult();
            batchSendResult.setTotalBoxNum(boxes.size());
            batchSendResult.setPackageBarNum(packages.size());

            List<BatchSendResult> data = new ArrayList<BatchSendResult>();
            data.add(batchSendResult);


            batchSendInfoResponse.setData(data);
            batchSendInfoResponse.setCode(JdResponse.CODE_OK);
            batchSendInfoResponse.setMessage(JdResponse.MESSAGE_OK);
        }catch (Throwable e){
            logger.error("查询发货原包数量与箱子数量",e);
            batchSendInfoResponse.setCode(InvokeResult.SERVER_ERROR_CODE);
            batchSendInfoResponse.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

		return batchSendInfoResponse;
	}

    /**
	 * 快生打印
	 */
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public BasicQueryEntityResponse basicPrintQueryOffline(PrintQueryCriteria criteria) {
		Date startDate = new Date();
	    logger.info("打印交接清单-基本信息查询开始"+DateHelper.formatDate(startDate));
	    BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
	    try {
		    SendM qSendM = tosendM(criteria);
	        List<SendM> sendMs =this.selectUniquesSendMs(qSendM);
	        if(sendMs!=null && !sendMs.isEmpty()){
	        	tBasicQueryEntityResponse = detailPrintQueryOffline(sendMs,criteria);	            
	        }
	    } catch (Exception e) {
            logger.error("打印明细基本查询异常");
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        }
		Date endDate = new Date();
		logger.info("打印交接清单-基本信息查询结束-"+(startDate.getTime() - endDate.getTime()));
		return tBasicQueryEntityResponse;
	}
	
	/**
	 * 快生明细打印
	 */
	public BasicQueryEntityResponse detailPrintQueryOffline(List<SendM> sendMs, PrintQueryCriteria criteria) {
		Date startDate = new Date();
		logger.info("打印交接清单-detailPrintQuery开始" + DateHelper.formatDate(startDate));

		BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
		List<BasicQueryEntity> tList = new ArrayList<BasicQueryEntity>();
		String rsiteName = toSiteName(criteria.getReceiveSiteCode());
		String fsiteName = toSiteName(criteria.getSiteCode());
		Integer rSiteType = toSiteType(criteria.getReceiveSiteCode());

		String message = JdResponse.MESSAGE_OK;
		for (SendM dendM : sendMs) {
			SendDetail tSendDatail = new SendDetail();
			tSendDatail.setCreateSiteCode(dendM.getCreateSiteCode());
			tSendDatail.setBoxCode(dendM.getBoxCode());
			tSendDatail.setReceiveSiteCode(dendM.getReceiveSiteCode());
			tSendDatail.setIsCancel(1);

			List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);

			if (sendDetails != null && !sendDetails.isEmpty()) {
				try {
					for (SendDetail dSendDatail : sendDetails) {
						if (criteria.getPackageBarcode() == null || "".equals(criteria.getPackageBarcode())
								|| criteria.getPackageBarcode().equals(dSendDatail.getPackageBarcode())) {
							BasicQueryEntity tBasicQueryEntity = new BasicQueryEntity();
							tBasicQueryEntity.setBoxCode(dendM.getBoxCode());
							if (dSendDatail.getIsCancel() != null && dSendDatail.getIsCancel() == 0) {
								tBasicQueryEntity.setIscancel("否");
							} else {
								tBasicQueryEntity.setIscancel("是");
							}
							tBasicQueryEntity.setIsnew("否");
							tBasicQueryEntity.setPackageBarWeight(0.0);
							tBasicQueryEntity.setPackageBarWeight2(0.0);
							tBasicQueryEntity.setPackageBar(dSendDatail.getPackageBarcode());
							tBasicQueryEntity.setReceiveSiteCode(dendM.getReceiveSiteCode());
							tBasicQueryEntity.setReceiveSiteName(rsiteName);
							tBasicQueryEntity.setSendCode(dendM.getSendCode());
							tBasicQueryEntity.setReceiveSiteType(rSiteType);
							tBasicQueryEntity.setSendSiteName(fsiteName);
							tBasicQueryEntity.setSendUser(dendM.getCreateUser());
							tBasicQueryEntity.setSendUserCode(dendM.getCreateUserCode());
							tBasicQueryEntity.setWaybill(dSendDatail.getWaybillCode());
							tBasicQueryEntity.setInvoice(dSendDatail.getPickupCode());

							if (dSendDatail.getWaybillCode() != null) {
								QuickProduceWabill tQuickProduceWabill = quickProduceService
										.getQuickProduceWabill(dSendDatail.getWaybillCode());
								if (tQuickProduceWabill == null) {
									logger.info("打印交接清单-tQuickProduceWabill为空");
									tList.add(tBasicQueryEntity);
									continue;
								}
								JoinDetail tJoinDetail = tQuickProduceWabill.getJoinDetail();
								if (tJoinDetail == null) {
									logger.info("打印交接清单-tJoinDetail为空");
									tList.add(tBasicQueryEntity);
									continue;
								}
								tBasicQueryEntity.setFcNo(tJoinDetail.getDistributeStoreId());
								tBasicQueryEntity.setDeclaredValue(String.valueOf(tJoinDetail.getDeclaredValue()));
								tBasicQueryEntity.setGoodValue(String.valueOf(tJoinDetail.getPrice()));
								tBasicQueryEntity.setGoodWeight(tJoinDetail.getGoodWeight());
								tBasicQueryEntity.setGoodWeight2(0.0);
								tBasicQueryEntity.setPackageBarNum(BusinessHelper.getPackageNum(dSendDatail.getPackageBarcode()));
								String sendPay = tJoinDetail.getSendPay();
								// 是否是奢侈品
								if (sendPay != null && sendPay.charAt(19) == '1') {
									tBasicQueryEntity.setLuxury("是");
								} else {
									tBasicQueryEntity.setLuxury("否");
								}

								Integer siteId = tJoinDetail.getOldSiteId();
								String siteName = null;
								BaseStaffSiteOrgDto bDto = this.baseMajorManager
										.getBaseSiteBySiteId(siteId);
								if (bDto != null) {
									siteName = bDto.getSiteName();
									Integer siteType = bDto.getSiteType();
									if (siteType != null && !siteType.equals(16)) {
										tBasicQueryEntity.setSiteType("自营");
									}
								}

								tBasicQueryEntity.setPayment(tJoinDetail.getPayment());
								if (rSiteType.equals(16)) {
									tBasicQueryEntity.setReceiverAddress(tJoinDetail.getReceiverAddress() == null ? ""
											: tJoinDetail.getReceiverAddress());
									String receiverMobile = tJoinDetail.getReceiverMobile() == null ? ""
											: tJoinDetail.getReceiverMobile();
									String receiverTel = tJoinDetail.getReceiverTel() == null ? ""
											: tJoinDetail.getReceiverTel();
									if (tJoinDetail.getReceiverMobile() == null
											&& tJoinDetail.getReceiverTel() == null) {
										tBasicQueryEntity.setReceiverMobile("--");
									} else {
										//tBasicQueryEntity.setReceiverMobile(receiverMobile + "/" + receiverTel);
										tBasicQueryEntity.setReceiverMobile("");
									}
								} else {
									tBasicQueryEntity.setReceiverMobile("--");
								}

								tBasicQueryEntity.setReceiverName(tJoinDetail.getReceiverName());
								if (tJoinDetail.getPayment() == null) {
									tBasicQueryEntity.setSendPay("");
								} else {
									tBasicQueryEntity.setSendPay(getSendPay(tJoinDetail.getPayment()));
									if (tJoinDetail.getPayment() != 1 && tJoinDetail.getPayment() != 3) {
										tBasicQueryEntity.setDeclaredValue("0");
									}
								}
								tBasicQueryEntity.setSiteCode(siteId);
								tBasicQueryEntity.setSiteName(siteName);
								if (tJoinDetail.getWaybillType() == null) {
									tBasicQueryEntity.setWaybillType("一般订单");
								} else {
									tBasicQueryEntity.setWaybillType(getWaybillType(tJoinDetail.getWaybillType()));
								}
							}

							tList.add(tBasicQueryEntity);
						}
					}
				} catch (Exception e) {
					message = "同步运单基本信息异常错误原因为" + e.getMessage();
					logger.error("同步运单基本信息异常错误原因为" + e.getMessage());
				}
			}
		}
		tBasicQueryEntityResponse.setCode(JdResponse.CODE_OK);
		tBasicQueryEntityResponse.setMessage(message);
		tBasicQueryEntityResponse.setData(tList);
		Date endDate = new Date();
		logger.info("打印交接清单-detailPrintQuery结束-" + (startDate.getTime() - endDate.getTime()));
		return tBasicQueryEntityResponse;
	}

}
