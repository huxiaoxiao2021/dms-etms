package com.jd.bluedragon.core.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.stock.iwms.export.StockExportService;
import com.jd.stock.iwms.export.param.StockVOParam;

@Service("stockExportManager")
public class StockExportManagerImpl implements StockExportManager {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private StockExportService stockExportService;
	
	@Override
	@SuppressWarnings("rawtypes")
	public long insertStockVirtualIntOut(StockVOParam stockVOParam0, StockVOParam stockVOParam1) {
		try{
			com.jd.stock.iwms.export.result.BaseResult result = stockExportService.insertStockVirtualIntOut(stockVOParam0, stockVOParam1);
			
			if(result!=null){
				if(!result.isResultFlag()){
					this.logger.error("调用库管接口stockExportManager.insertStockVirtualIntOut异常：result:"+result.getMessage());
					return 0;
				}else{
					this.logger.info("调用库管接口stockExportManager.insertStockVirtualIntOut成功：resultCode:"+result.getResultCode()+" resultMessage:"+result.getMessage());
					return result.getKdanHao();
				}
			}else{
				this.logger.error("调用库管接口stockExportManager.insertStockVirtualIntOut异常: result为空!");
				return 0;
			}
			
		}catch(Exception e){
			logger.error("调用库管接口stockExportManager.insertStockVirtualIntOut异常", e);
			return 0;
		}
	}
}
