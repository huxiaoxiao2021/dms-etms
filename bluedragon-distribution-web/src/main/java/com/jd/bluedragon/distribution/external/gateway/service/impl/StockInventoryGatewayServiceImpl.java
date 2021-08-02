package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.stock.StockInventoryResult;
import com.jd.bluedragon.common.dto.stock.StockInventoryScanDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.stock.service.StockInventoryService;
import com.jd.bluedragon.external.gateway.service.StockInventoryGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 库存盘点网关服务实现
 *
 * @author hujiping
 * @date 2021/6/4 5:15 下午
 */
@Service
public class StockInventoryGatewayServiceImpl implements StockInventoryGatewayService {

    @Autowired
    private StockInventoryService stockInventoryService;

    @JProfiler(jKey = "DMSWEB.StockInventoryGatewayServiceImpl.queryStockInventory", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public JdCResponse<StockInventoryResult> queryStockInventory(Integer createSiteCode) {
        JdCResponse<StockInventoryResult> jdCResponse = new JdCResponse<StockInventoryResult>();
        jdCResponse.toSucceed();
        InvokeResult<StockInventoryResult> result = stockInventoryService.queryStockInventory(createSiteCode);
        if(!result.codeSuccess()){
            jdCResponse.setCode(result.getCode());
            jdCResponse.setMessage(result.getMessage());
            return jdCResponse;
        }
        jdCResponse.setData(result.getData());
        return jdCResponse;
    }

    @JProfiler(jKey = "DMSWEB.StockInventoryGatewayServiceImpl.stockInventoryScan", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public JdCResponse<StockInventoryResult> stockInventoryScan(StockInventoryScanDto stockInventoryScanDto) {
        JdCResponse<StockInventoryResult> jdCResponse = new JdCResponse<StockInventoryResult>();
        InvokeResult<StockInventoryResult> result = stockInventoryService.stockInventoryScan(stockInventoryScanDto);
        if(!result.codeSuccess()){
            jdCResponse.setCode(result.getCode());
            jdCResponse.setMessage(result.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed();
        jdCResponse.setData(result.getData());
        return jdCResponse;
    }
}
