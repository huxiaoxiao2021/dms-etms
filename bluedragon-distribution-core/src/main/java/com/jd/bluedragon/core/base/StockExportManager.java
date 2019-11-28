package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.stock.iwms.export.param.StockVOParam;

public interface StockExportManager{


    long insertStockVirtualIntOut(StockVOParam stockVOParam0, StockVOParam stockVOParam1);
	
    /*******************方法扩展区*********************/
	KuGuanDomain queryByOrderCode(String orderCode,String lKdanhao);
	
	/**
	 * 已经做了异常处理,只以运单号做库查询条件
	 * @param waybillCode
	 * @return 返回一个KuGuanDomain对象,如果查不到对应运单的库管对象,则返回null
	 */
	public KuGuanDomain queryByWaybillCode(String waybillCode);
}
