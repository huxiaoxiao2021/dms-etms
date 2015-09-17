package com.jd.bluedragon.distribution.globaltrade.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;

public class LoadBillReadDao extends BaseDao<LoadBill> {

	private final Log logger = LogFactory.getLog(this.getClass());

	public static final String namespace = LoadBillReadDao.class.getName();

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LoadBill> findPageLoadBill(Map<String, Object> params) {
		return (List<LoadBill>) super.getSqlSessionRead().selectList(LoadBillReadDao.namespace + ".findPage", params);
	}

	public Integer findCountLoadBill(Map<String, Object> params) {
		return (Integer) super.getSqlSessionRead().selectOne(LoadBillReadDao.namespace + ".findCount", params);
	}
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<LoadBill> findWaybillInLoadBill(Map<String, Object> params) {
        return (List<LoadBill>) super.getSqlSessionRead().selectList(LoadBillReadDao.namespace + ".findWaybillinLoadBill", params);
    }


}
