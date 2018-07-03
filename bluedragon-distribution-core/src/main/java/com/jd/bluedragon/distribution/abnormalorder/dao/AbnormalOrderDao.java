package com.jd.bluedragon.distribution.abnormalorder.dao;

import com.google.common.collect.Maps;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	 public List<AbnormalOrder> queryByWaveIds(List<String> waveIds){
		 return super.getSqlSession().selectList(namespace + ".getByWaveIds" , waveIds);
	 }
	/**
	 * 通过班次id和 多运单，查异常处理情况
	 * @param waveBusinessId
	 * @param waybillCodeList
	 * @return
	 */
	public List<AbnormalOrder> queryByWaveIdAndWaybillCodes(String waveBusinessId, ArrayList<String> waybillCodeList) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("waveBusinessId", waveBusinessId);
		param.put("waybillCodes", waybillCodeList);
		return super.getSqlSession().selectList(namespace + ".queryByWaveIdAndWaybillCodes", param);
	}
}
