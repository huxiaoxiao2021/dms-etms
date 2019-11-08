package com.jd.bluedragon.distribution.abnormalorder.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.jd.bluedragon.distribution.api.response.RefundReason;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.ql.basic.domain.BaseDataDict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbnormalOrderUtil {
    
	private static final String SYSCONFIG_REFUNDREASONCODEKEY_KEY = "abnormalorder.refundreason%";

	private static AbnormalOrderUtil abnormalOrderUtil = new AbnormalOrderUtil();
	
	private static RefundReason[] refundReason = null;
	
    public synchronized static void init(BaseService baseService){
    	Logger log = LoggerFactory.getLogger(AbnormalOrderServiceImpl.class);
    	Integer[] refundReasonCode = null;
    	/************************* 处理基础参数  ************************/    	
    	log.info("AbnormalOrderUtil.init refunReasonSysconfigList = baseService.queryConfigByKey(SYSCONFIG_REFUNDREASONCODEKEY_KEY)");
    	List<SysConfig> refunReasonSysconfigList = baseService.queryConfigByKey(SYSCONFIG_REFUNDREASONCODEKEY_KEY);
    	if(refunReasonSysconfigList!=null){
    		log.info("AbnormalOrderUtil.init refunReasonSysconfigList size:{}" , refunReasonSysconfigList.size());
    		SysConfig[] refunReasonSysconfigs = refunReasonSysconfigList.toArray(new SysConfig[0]);
	    	Arrays.sort(refunReasonSysconfigs, new Comparator<SysConfig>() {
	
				@Override
				public int compare(SysConfig o1, SysConfig o2) {
					// TODO Auto-generated method stub
					return o1.getConfigOrder().compareTo(o2.getConfigOrder());
				}
			});
	    	
	    	refundReasonCode = new Integer[refunReasonSysconfigs.length];
	    	
	    	for(int i = 0;i<refunReasonSysconfigs.length;i++){
	    		log.info("AbnormalOrderUtil.init refunReasonSysconfigList[{}]={}" ,i, refunReasonSysconfigs[i].getConfigContent() );
	    		refundReasonCode[i] = Integer.parseInt(refunReasonSysconfigs[i].getConfigContent());
	    	}
    	}else{
    		log.info("AbnormalOrderUtil.init refunReasonSysconfigList is null");
    		refundReasonCode = new Integer[0];
    	}
    	
    	/************************* 处理退货类型  ************************/
    	List<Integer> codeAl = Arrays.asList(refundReasonCode);
		RefundReason[] refundReasons = new RefundReason[refundReasonCode.length];
		log.info("AbnormalOrderUtil.init 分拣退货类型查询  baseService.getBaseDataDictList(?,3,5003)");
		List<BaseDataDict> resultal = baseService.getBaseDataDictList(5003,2,5003);
		
		if(codeAl.size()==0){
			refundReason = new RefundReason[0];
		}
		
		log.info("AbnormalOrderUtil.init refundReasons根据refunReasonSysconfigList进行处理");
		for(BaseDataDict data : resultal){
			int index = codeAl.indexOf(data.getTypeCode());
			if(index>=0){
				if(refundReasons[index]==null){
					refundReasons[index] = new RefundReason(data.getTypeCode(), data.getTypeName());
				}
				
				List<BaseDataDict> tmpal = baseService.getBaseDataDictList(data.getTypeCode(),3,5003);
				if(tmpal!=null){
					for(BaseDataDict childDate:tmpal){
						refundReasons[index].addChild(childDate.getTypeCode(), childDate.getTypeName());
					}
				}
			}
		}
		log.info("AbnormalOrderUtil.init refundReasons.size:{}" , refundReasons.length);
		refundReason = refundReasons;
    }
    
    public static RefundReason[] getRefundReason(BaseService baseService){
    	if(refundReason==null){
    		init(baseService);
    	}
    	
    	return refundReason!=null?refundReason.clone():null;
    }
    
    public static AbnormalOrderUtil getInstance(){
    	return abnormalOrderUtil;
    }
    
    public static void clear(){
    	refundReason = null;
    }
}
