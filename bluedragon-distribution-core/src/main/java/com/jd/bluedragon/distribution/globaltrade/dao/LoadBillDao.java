package com.jd.bluedragon.distribution.globaltrade.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.verifier.statics.LONG_Upper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import org.omg.CORBA.OBJ_ADAPTER;

public class LoadBillDao extends BaseDao<LoadBill> {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final String namespace = LoadBillDao.class.getName();

	public int updateLoadBillStatus(Map<String, Object> loadBillStatusMap) {
		logger.info("LoadBillDao.updateLoadBillStatus loadId is " + loadBillStatusMap.get("loadId") + ", orderId is " + loadBillStatusMap.get("orderIdList").toString());
		return this.getSqlSession().update(LoadBillDao.namespace + ".updateLoadBillStatus", loadBillStatusMap);
	}

	public int addBatch(List<LoadBill> loadBillList) {
		logger.info("LoadBillDao.addBatch with number of loadBillList is " + loadBillList.size());
		return this.getSqlSession().insert(LoadBillDao.namespace + ".addBatch", loadBillList);
	}

    public List<LoadBill> getLoadBills(List<Long> billId){
        return this.getSqlSession().selectList(LoadBillDao.namespace + ".selectLoadBillById", billId);
    }

    public Long selectPreLoadBillId(){
        return (Long) this.getSqlSession().selectOne(LoadBillDao.namespace + ".selectPreLoadBillId");
    }

    public int updatePreLoadBillById(List<Long> billId,String trunkNo,String loadId,Integer approvalCode){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("data",billId);
        map.put("loadId",loadId);
        map.put("trunkNo", trunkNo);
        map.put("approvalCode", approvalCode);
        return this.getSqlSession().update(LoadBillDao.namespace + ".updateLoadBillById", map);
    }
	public LoadBill findByPackageBarcode(String packageBarcode) {
		logger.info("LoadBillDao.getLoadBill with packageBarcode is " + packageBarcode);
		return (LoadBill) this.getSqlSession().selectOne(LoadBillDao.namespace + ".findByPackageBarcode", packageBarcode);
	}

	public int add(LoadBill lb) {
		return this.getSqlSession().insert(LoadBillDao.namespace + ".add", lb);
	}

	public int update(LoadBill lb) {
		return this.getSqlSession().update(LoadBillDao.namespace + ".update", lb);
	}

	public LoadBill findLoadbillByID(Long id) {
        return (LoadBill) super.getSqlSession().selectOne(LoadBillDao.namespace + ".findLoadbillByID", id);
    }
	
	public int updateCancelLoadBillStatus(LoadBill loadBill) {
		return this.getSqlSession().update(LoadBillDao.namespace + ".updateCancelLoadBillStatus", loadBill);
	}
}
