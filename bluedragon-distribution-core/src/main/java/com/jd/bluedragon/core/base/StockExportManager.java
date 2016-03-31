package com.jd.bluedragon.core.base;

import java.util.Map;

import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.stock.iwms.export.param.StockVOParam;
import com.jd.stock.iwms.export.result.QueryResult;
import com.jd.stock.iwms.export.vo.StockVO;

public interface StockExportManager{

	/**
	 * @param stockVOParam0
	 * @param stockVOParam1
	 * @return
	 */
	public abstract long insertStockVirtualIntOut(StockVOParam stockVOParam0, StockVOParam stockVOParam1);

	/**
	 * 根据业务单号查询完整的出管数据，包括出管、扩展和清单
	 * @param BusinessNo   业务单号（必填）
	 * @param BusinessType 业务类型（可为null，不检索类型）
	 * @param isQueryHis   是否查询历史（可为null，只查线上）
	 * @param sysTag       系统标识（必填）
	 * @return QueryResult
	 * @author 潘文华 2015-8-17
	 */
	public QueryResult<StockVO> getFullStockByBusiNo(String businessNo,Integer businessType,Boolean isQueryHis);
	
    /*******************方法扩展区*********************/
	KuGuanDomain queryByParams(Map<String, Object> paramMap);
	
	/**
	 * 已经做了异常处理,只以运单号做库查询条件
	 * @param waybillCode
	 * @return 返回一个KuGuanDomain对象,如果查不到对应运单的库管对象,则返回null
	 */
	public KuGuanDomain queryByWaybillCode(String waybillCode);
}
