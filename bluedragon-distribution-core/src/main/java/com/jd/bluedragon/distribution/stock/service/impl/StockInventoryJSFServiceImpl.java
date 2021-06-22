package com.jd.bluedragon.distribution.stock.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.stock.StockInventoryJSFService;
import com.jd.bluedragon.distribution.stock.domain.InventoryQuery;
import com.jd.bluedragon.distribution.stock.domain.StockInventory;
import com.jd.bluedragon.distribution.stock.service.StockInventoryService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 清场库存盘点对外jsf接口实现
 *
 * @author hujiping
 * @date 2021/6/7 4:48 下午
 */
@Service("stockInventoryJSFService")
public class StockInventoryJSFServiceImpl implements StockInventoryJSFService {

    @Autowired
    private StockInventoryService stockInventoryService;

    @JProfiler(jKey = "dms.stock.StockInventoryJSFServiceImpl.queryInventoryUnSendPacks", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Response<List<StockInventory>> queryInventoryUnSendPacks(InventoryQuery queryCondition) {
        Response<List<StockInventory>> response = new Response<List<StockInventory>>();
        InvokeResult<List<StockInventory>> result = stockInventoryService.queryInventoryUnSendPacks(queryCondition);
        if(result == null || CollectionUtils.isEmpty(result.getData())){
            response.toError("结果为空!");
            return response;
        }
        response.toSucceed();
        response.setData(result.getData());
        return response;
    }

    @JProfiler(jKey = "dms.stock.StockInventoryJSFServiceImpl.queryInventoryUnSendNum", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Response<Long> queryInventoryUnSendNum(InventoryQuery queryCondition) {
        Response<Long> response = new Response<Long>();
        InvokeResult<Long> result = stockInventoryService.queryInventoryUnSendNum(queryCondition);
        if(result == null || result.getData() == null){
            response.toError("结果为空!");
            return response;
        }
        response.toSucceed();
        response.setData(result.getData());
        return response;
    }
}
