package com.jd.bluedragon.distribution.globaltrade.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;

public class LoadBillDao extends BaseDao<LoadBill>{

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final String namespace = LoadBillDao.class.getName();
	
	public int updateLoadBillStatus(Map<String, Object> loadBillStatusMap) {
		logger.info("LoadBillDao.updateLoadBillStatus orderId is " + loadBillStatusMap.get("orderId"));
		return this.getSqlSession().update(LoadBillDao.namespace + "updateLoadBillStatus", loadBillStatusMap);
	}

    public List<LoadBill> getLoadBills(List<Integer> billId){
        return this.getSqlSession().selectList(LoadBillDao.namespace + ".selectById", billId);
    }
}
