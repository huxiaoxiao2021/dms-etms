package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.ReverseReceiveRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.reverse.dao.ReverseReceiveDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceiveLoss;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.Pickware;
import com.jd.bluedragon.distribution.waybill.service.PickwareService;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("reverseReceiveService")
public class ReverseReceiveServiceImpl implements ReverseReceiveService {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ReverseReceiveDao reverseReceiveDao;
    
    @Autowired
    private PickwareService pickwareService;
    
    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    @Qualifier("logisticsCompensationMQ")
    private DefaultJMQProducer logisticsCompensationMQ;

    @Autowired
    @Qualifier("dmsSendLossMQ")
    private  DefaultJMQProducer dmsSendLossMQ;

    @Autowired
    private BaseMajorManager baseMajorManager;
    
	@Autowired
	private SendMDao sendMDao;
	
	 @Autowired
    private SendDatailDao sendDatailDao;
	
	@Autowired
	private ReverseSpareService reverseSpareService;



    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer add(ReverseReceive reverseReceive) {
        return this.reverseReceiveDao.add(ReverseReceiveDao.namespace, reverseReceive);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer update(ReverseReceive reverseReceive) {
        return this.reverseReceiveDao.update(ReverseReceiveDao.namespace, reverseReceive);
    }
    

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ReverseReceive findByPackageCode(String packageCode) {
        return this.reverseReceiveDao.findByPackageCode(packageCode);
    }
    

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ReverseReceive findByPackageCodeAndSendCode(String packageCode,String sendCode,Integer businessType) {
    	return this.reverseReceiveDao.findByPackageCodeAndSendCode(packageCode, sendCode, businessType);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ReverseReceive findMCS(String packageCode,String sendCode,Integer businessType, Integer canReceive) {
        return this.reverseReceiveDao.findMCS(packageCode, sendCode, businessType, canReceive);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void aftersaleReceive(ReverseReceive source) {
        if (StringHelper.isEmpty(source.getPackageCode())) {
            this.logger.info("数据不合法.");
            return;
        }
        
        if(source.getReceiveType()==2){
            ReverseReceive reverseReceivePO = this.findByPackageCodeAndSendCode(source.getPackageCode(),source.getSendCode(),source.getReceiveType());
            if (reverseReceivePO == null) {
            	this.logger.info("reverseReceivePO is null");
                this.appentPickwareInfo(source, source.getPackageCode());
                this.add(source);
                this.addOpetationLog(source, OperationLog.TYPE_REVERSE_RECEIVE,"add");
            } else {
            	this.logger.info("reverseReceivePO is not null");
                ReverseReceive reverseReceiveVO = new ReverseReceive();
                BeanHelper.copyProperties(reverseReceiveVO, source);
                reverseReceiveVO.setId(reverseReceivePO.getId());
                reverseReceiveVO.setReceiveType(source.getReceiveType());
                reverseReceiveVO.setReceiveTime(source.getReceiveTime());
                this.appentPickwareInfo(reverseReceiveVO, source.getPackageCode());
                this.update(reverseReceiveVO);
                this.addOpetationLog(reverseReceiveVO, OperationLog.TYPE_REVERSE_RECEIVE,"update");
            } 
        }else if(source.getReceiveType()==1||source.getReceiveType()==5){
            ReverseReceive reverseReceivePO = this.findByPackageCodeAndSendCode(source.getPackageCode(),source.getSendCode(),source.getReceiveType());
            if (reverseReceivePO == null) {
            	this.logger.info("reverseReceivePO is null");
                this.appentPickwareInfo(source, source.getPackageCode());
                this.add(source);
                this.addOpetationLog(source, OperationLog.TYPE_REVERSE_RECEIVE,"add");
            } else {
            	this.logger.info("reverseReceivePO is not null");
                ReverseReceive reverseReceiveVO = new ReverseReceive();
                BeanHelper.copyProperties(reverseReceiveVO, source);
                reverseReceiveVO.setId(reverseReceivePO.getId());
                reverseReceiveVO.setReceiveType(source.getReceiveType());
                reverseReceiveVO.setReceiveTime(source.getReceiveTime());
                this.appentPickwareInfo(reverseReceiveVO, source.getPackageCode());
                this.update(reverseReceiveVO);
                this.addOpetationLog(reverseReceiveVO, OperationLog.TYPE_REVERSE_RECEIVE,"update");
            } 
        }  else if(source.getReceiveType()==3) {
        	ReverseReceive reverseReceivePO = this.findByPackageCodeAndSendCode(source.getOrderId(),source.getSendCode(),source.getReceiveType());
            if (reverseReceivePO == null) {
            	this.logger.info("reverseReceivePO is null");
                this.appentPickwareInfo(source, source.getPackageCode());
                this.add(source);
                this.addOpetationLog(source, OperationLog.TYPE_REVERSE_RECEIVE,"add");
            } else {
            	this.logger.info("reverseReceivePO is not null");
                ReverseReceive reverseReceiveVO = new ReverseReceive();
                BeanHelper.copyProperties(reverseReceiveVO, source);
                reverseReceiveVO.setId(reverseReceivePO.getId());
                reverseReceiveVO.setReceiveType(source.getReceiveType());
                reverseReceiveVO.setReceiveTime(source.getReceiveTime());
                this.appentPickwareInfo(reverseReceiveVO, source.getPackageCode());
                this.update(reverseReceiveVO);
                this.addOpetationLog(reverseReceiveVO, OperationLog.TYPE_REVERSE_RECEIVE,"update");
            }
            
            SendDetail query = new SendDetail();
            query.setWaybillCode(source.getOrderId());
            List<SendDetail>  sendDetails=sendDatailDao.querySendDatailsBySelective(query);
            if(sendDetails!=null&&!sendDetails.isEmpty()){
            	 for (SendDetail tSendDetail : sendDetails) {
                     if (tSendDetail.getFeatureType()!=null&&2==tSendDetail.getFeatureType().intValue()) {
                    	  //三方七折(推送财务mq)
                         ReverseReceive sendVo=new ReverseReceive();
                         sendVo.setBusinessDate(source.getReceiveTime());
                         sendVo.setWaybillCode(source.getOrderId());
                         try{
                     		    //messageClient.sendMessage("logisticsCompensation", JsonHelper.toJson(sendVo),sendVo.getWaybillCode());
                                logisticsCompensationMQ.send(sendVo.getWaybillCode(),JsonHelper.toJson(sendVo));
                     		}catch (Exception e) {
                     			this.logger.error("分拣中心逆向收货:备件库收货[三方七折]推送财务mq信息失败：" + e.getMessage(),e);
                     		}
                         break;
                     }
                }
            }
        } else if (source.getReceiveType() == 4) { //维修外单售后
            //维修外单:没有0值表示拒收,除了维持1值表示接收,还增加了2值表示交接
            //0和1是平级,要么0,要么1,数据库只会有一条数据;但是3是交接,发生了在0/1之前,所以数据库会存在两条数据
            //方案:另外声明一个查询方法,加入新的过滤条件,canReceive,可以确定唯一数据
            ReverseReceive reverseReceivePO = this.findMCS(source.getPackageCode(), source.getSendCode(), source.getReceiveType(), source.getCanReceive());
            if (reverseReceivePO == null) {
                this.logger.info("reverseReceivePO is null");
                if (StringUtils.isBlank(source.getPickWareCode())) {
                    this.appentPickwareInfo(source, source.getPackageCode());
                }
                this.add(source);
                this.addOpetationLog(source, OperationLog.TYPE_REVERSE_RECEIVE,"add");
            } else {
                this.logger.info("reverseReceivePO is not null");
                ReverseReceive reverseReceiveVO = new ReverseReceive();
                BeanHelper.copyProperties(reverseReceiveVO, source);
                reverseReceiveVO.setId(reverseReceivePO.getId());
                reverseReceiveVO.setReceiveType(source.getReceiveType());
                reverseReceiveVO.setReceiveTime(source.getReceiveTime());
                this.appentPickwareInfo(reverseReceiveVO, source.getPackageCode());
                this.update(reverseReceiveVO);
                this.addOpetationLog(reverseReceiveVO, OperationLog.TYPE_REVERSE_RECEIVE, "update");
            }
        }

    	sendReportLoss(source);
    }
    
    /**
     * 备件库和仓库收货后回传MQ消息给报损系统
     */
    private void sendReportLoss(ReverseReceive source) {
    	ReverseReceiveLoss reverseReceiveLoss = new ReverseReceiveLoss();
    	String orderId = source.getOrderId();
    	try {
	    	String updateDate = DateHelper.formatDateTime(source.getReceiveTime());
	    	Integer receiveType = source.getReceiveType();
	    	
	    	String dmsId=null;
	
	    	String dmsName=null;
	    	
	    	String storeId=null;
	    	
	    	String storeName=null;
	    	
	    	//仓储收货回传
	    	if(receiveType==1||source.getReceiveType()==5){
	    		SendM sendM =sendMDao.selectBySendCode(source.getSendCode());
	    		BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(sendM.getCreateSiteCode());
	    		dmsId = dto.getSiteCode().toString();
	    		dmsName = dto.getSiteName();
	    		BaseStaffSiteOrgDto dto1 = baseMajorManager.getBaseSiteBySiteId(sendM.getReceiveSiteCode());
	    		storeId = dto1.getSiteCode().toString();
	    		storeName = dto1.getSiteName();
	    	}else if(receiveType==3){
	    		List<ReverseSpare> reverseSpareList = reverseSpareService.queryBySpareTranCode(source.getSendCode());
	    		SendM sendM =sendMDao.selectBySendCode(reverseSpareList.get(0).getSendCode());
	    		BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(sendM.getCreateSiteCode());
	    		dmsId = dto.getSiteCode().toString();
	    		dmsName = dto.getSiteName();
	    		BaseStaffSiteOrgDto dto1 = baseMajorManager.getBaseSiteBySiteId(sendM.getReceiveSiteCode());
	    		storeId = dto1.getSiteCode().toString();
	    		storeName = dto1.getSiteName();
	    	}
	    	reverseReceiveLoss.setOrderId(orderId);
	    	reverseReceiveLoss.setReceiveType(receiveType);
	    	reverseReceiveLoss.setUpdateDate(updateDate);
	    	
	    	reverseReceiveLoss.setDmsId(dmsId);
	    	reverseReceiveLoss.setDmsName(dmsName);
	    	reverseReceiveLoss.setStoreId(storeId);
	    	reverseReceiveLoss.setStoreName(storeName);
	    	
	    	//根据收货与否进行报损锁定与否
	        if(ReverseReceive.RECEIVE.equals(source.getCanReceive())){
		    	reverseReceiveLoss.setIsLock(ReverseReceiveLoss.LOCK);
	        }else{
	        	reverseReceiveLoss.setIsLock(ReverseReceiveLoss.UNLOCK);
	        }
	        
	        String jsonStr = JsonHelper.toJson(reverseReceiveLoss);
	    	logger.error("青龙逆向发货后回传报损系统MQ orderid为" + orderId);
	    	logger.error("青龙逆向发货后回传报损系统MQ json为"+jsonStr);
    	
			//this.messageClient.sendMessage("dms_send_loss", jsonStr, orderId);
            dmsSendLossMQ.send(orderId,jsonStr);
			logger.info("青龙逆向发货后回传报损系统MQ消息成功，订单号为" + orderId);
		} catch (Exception e) {
			logger.error("青龙逆向发货后回传报损系统MQ消息失败，订单号为" + orderId, e);
		}
		
	}

	private void appentPickwareInfo(ReverseReceive reverseReceive, String code) {
        Pickware pickware = this.pickwareService.get(code);
        if (pickware != null) {
            reverseReceive.setOrderId(pickware.getWaybillCode().toString());
            reverseReceive.setPickWareCode(pickware.getCode());
        }
    }

    public void addOpetationLog(ReverseReceive reverseReceive, Integer logType,String remark) {
        this.appentPickwareInfo(reverseReceive, reverseReceive.getPackageCode());
        OperationLog operationLog = new OperationLog();
        operationLog.setWaybillCode(reverseReceive.getOrderId());
        operationLog.setPickupCode(reverseReceive.getPickWareCode());
        operationLog.setPackageCode(reverseReceive.getPackageCode());
        operationLog.setSendCode(reverseReceive.getSendCode());
        operationLog.setOperateTime(reverseReceive.getReceiveTime());
        operationLog.setCreateUser(reverseReceive.getOperatorName());
        operationLog.setRemark(remark);
        operationLog.setLogType(logType);
        this.operationLogService.add(operationLog);
    }
    
}
