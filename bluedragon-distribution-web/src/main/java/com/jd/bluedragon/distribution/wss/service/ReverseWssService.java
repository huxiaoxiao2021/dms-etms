package com.jd.bluedragon.distribution.wss.service;

public interface ReverseWssService {
	
	/** 仓储驳回 */
	Boolean addRejectMessage(String message);
	
	/** 备件库收货回传POP */
	Boolean addReceivePopMessage(String message);
	
	/** 备件库收货回传库存 */
	Boolean addReceiveStockMessage(String message) throws Exception;
	
	/** 备件库销售 */
	Boolean addSaleMessage(String message);
	
}
