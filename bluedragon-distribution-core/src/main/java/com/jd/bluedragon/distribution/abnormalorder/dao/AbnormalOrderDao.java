package com.jd.bluedragon.distribution.abnormalorder.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;

import java.util.List;

public class AbnormalOrderDao  extends BaseDao<AbnormalOrder>{
	 public static final String namespace = AbnormalOrderDao.class.getName();
	 
	 public int updateResult(AbnormalOrder abnormalOrder){
		 return super.getSqlSession().update(namespace + ".updateResult" , abnormalOrder);
	 }
	 
	 public int updateSome(AbnormalOrder abnormalOrder){
		 return super.getSqlSession().update(namespace + ".update" , abnormalOrder);
	 }
	 
	 public int insert(AbnormalOrder abnormalOrder){
		 return super.getSqlSession().insert(namespace + ".add" , abnormalOrder);
	 }
	 
	 public AbnormalOrder query(String orderId){
		 return (AbnormalOrder)super.getSqlSession().selectOne(namespace + ".get" , orderId);
	 }
	 public List<AbnormalOrder> queryByorderIds(List<String> orderIds){
		 return super.getSqlSession().selectList(namespace + ".getByorderIds" , orderIds);
	 }

}
