package com.jd.bluedragon.distribution.record.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.record.dto.WaybillHasnoPresiteRecordQo;
import com.jd.bluedragon.distribution.record.model.WaybillHasnoPresiteRecord;

/**
 * Description: 无滑道查询<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 */
public class WaybillHasnoPresiteRecordDao extends BaseDao<WaybillHasnoPresiteRecord> {

    private final static String NAMESPACE = WaybillHasnoPresiteRecordDao.class.getName();

    
    /**
     * 插入一条数据
     * @param waybillHasnoPresiteRecord
     * @return
     */
    public int insert(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
        return this.getSqlSession().insert(WaybillHasnoPresiteRecordDao.NAMESPACE + ".insert", waybillHasnoPresiteRecord);
    } 
    /**
     * 更新一条数据
     * @param waybillHasnoPresiteRecord
     * @return
     */
    public int update(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
        return this.getSqlSession().update(WaybillHasnoPresiteRecordDao.NAMESPACE + ".update", waybillHasnoPresiteRecord);
    }
    /**
     * 根据运单号查询
     * @param waybillCode
     * @return
     */
    public WaybillHasnoPresiteRecord queryByWaybillCode(String waybillCode) {
        return this.getSqlSession().selectOne(WaybillHasnoPresiteRecordDao.NAMESPACE + ".queryByWaybillCode", waybillCode);
    }
    /**
     * 按条件查询总数
     * @param query 查询参数
     * @return 数据列表
     */
    public Long selectCount(WaybillHasnoPresiteRecordQo query) {
        return this.getSqlSession().selectOne(WaybillHasnoPresiteRecordDao.NAMESPACE + ".selectCount", query);
    }

    /**
     * 按条件查询列表
     * @param query 查询参数
     * @return 数据列表
     */
    public List<WaybillHasnoPresiteRecord> selectList(WaybillHasnoPresiteRecordQo query) {
        return this.getSqlSession().selectList(WaybillHasnoPresiteRecordDao.NAMESPACE + ".selectList", query);
    }
    /**
     * 更新验货信息
     * @param waybillHasnoPresiteRecord
     * @return
     */
	public int updateCheckInfo(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		return this.getSqlSession().update(WaybillHasnoPresiteRecordDao.NAMESPACE + ".updateCheckInfo", waybillHasnoPresiteRecord);
	}
	/**
	 * 更新外呼状态
	 * @param waybillHasnoPresiteRecord
	 * @return
	 */
	public int updateCallInfo(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		return this.getSqlSession().update(WaybillHasnoPresiteRecordDao.NAMESPACE + ".updateCallInfo", waybillHasnoPresiteRecord);
	}
    /**
     * 更新完成信息
     * @param waybillHasnoPresiteRecord
     * @return
     */
	public int updateFailFinishInfo(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		return this.getSqlSession().update(WaybillHasnoPresiteRecordDao.NAMESPACE + ".updateFailFinishInfo", waybillHasnoPresiteRecord);
	}
    /**
     * 更新完成信息
     * @param waybillHasnoPresiteRecord
     * @return
     */
	public int updateSucFinishInfo(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		return this.getSqlSession().update(WaybillHasnoPresiteRecordDao.NAMESPACE + ".updateSucFinishInfo", waybillHasnoPresiteRecord);
	}	
	/**
	 * 查询扫描数据
	 * @param query
	 * @return
	 */
	public List<WaybillHasnoPresiteRecord> selectScanList(WaybillHasnoPresiteRecordQo query) {
		return this.getSqlSession().selectList(WaybillHasnoPresiteRecordDao.NAMESPACE + ".selectScanList", query);
	}
}
