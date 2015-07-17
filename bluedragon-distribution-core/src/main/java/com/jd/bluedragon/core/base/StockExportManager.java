package com.jd.bluedragon.core.base;

import com.jd.stock.iwms.export.param.StockVOParam;

public interface StockExportManager{

	/**
	 * @param stockVOParam0
	 * @param stockVOParam1
	 * @return
	 */
	public abstract long insertStockVirtualIntOut(StockVOParam stockVOParam0, StockVOParam stockVOParam1);
}
