package com.jd.bluedragon.distribution.reverse.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;

public class ReverseReceiveDao extends BaseDao<ReverseReceive> {
    
    public static final String namespace = ReverseReceiveDao.class.getName();
    
    public ReverseReceive findByOrderId(Long orderId) {
        return (ReverseReceive) super.getSqlSession().selectOne(
                ReverseReceiveDao.namespace + ".findByOrderId", orderId);
    }
    
    public ReverseReceive findByPackageCode(String packageCode) {
        return (ReverseReceive) super.getSqlSession().selectOne(
                ReverseReceiveDao.namespace + ".findByPackageCode", packageCode);
    }
    
    public ReverseReceive findByPackageCodeAndSendCode(String packageCode,String sendCode,Integer businessType) {
    	ReverseReceive reverseReceive = new ReverseReceive();
    	reverseReceive.setPackageCode(packageCode);
    	reverseReceive.setSendCode(sendCode);
    	reverseReceive.setReceiveType(businessType);
    	return (ReverseReceive) super.getSqlSession().selectOne(
    			ReverseReceiveDao.namespace + ".findByPackageCodeAndSendCode", reverseReceive);
    }
    
    public ReverseReceive findByWaybillCodeAndSendCode(String waybillCode,String sendCode) {
    	ReverseReceive reverseReceive = new ReverseReceive();
    	reverseReceive.setPackageCode(waybillCode);
    	reverseReceive.setSendCode(sendCode);
    	return (ReverseReceive) super.getSqlSession().selectOne(
    			ReverseReceiveDao.namespace + ".findByWaybillCodeAndSendCode", reverseReceive);
    }
    
}
