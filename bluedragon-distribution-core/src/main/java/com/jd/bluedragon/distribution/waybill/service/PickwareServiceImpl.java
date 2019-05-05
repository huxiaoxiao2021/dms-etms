package com.jd.bluedragon.distribution.waybill.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.waybill.domain.Pickware;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PickupTask;

@Service("pickwareService")
public class PickwareServiceImpl implements PickwareService {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
	private WaybillPickupTaskApi waybillPickupTaskApi;
    
    public Pickware get(String code) {
        this.logger.info("取件单面单号：" + code);
        
        BaseEntity<PickupTask> pickupTask = this.waybillPickupTaskApi.getDataBySfCode(code);
        if (pickupTask == null || pickupTask.getData() == null) {
            this.logger.info("未找到此面单号相关信息, 取件单面单号：" + code);
            return null;
        }
        
        Pickware pickware = new Pickware(); // 取件单内商品
        
        if (StringHelper.isNotEmpty(pickupTask.getData().getProductName())) {
            pickware.setProductName(StringHelper.getStringValue(pickupTask.getData()
                    .getProductName()));
        }
        
        if (NumberHelper.isPositiveNumber(pickupTask.getData().getAmount())) {
            pickware.setQuantity(pickupTask.getData().getAmount());
        }
        
        if (StringHelper.isNotEmpty(pickupTask.getData().getProductCode())) {
            pickware.setProductCode(StringHelper.getStringValue(pickupTask.getData()
                    .getProductCode()));
        }
        
        if (StringHelper.isNotEmpty(pickupTask.getData().getPickupCode())) {
            pickware.setCode(StringHelper.getStringValue(pickupTask.getData().getPickupCode()));
        }
        
        if (StringHelper.isNotEmpty(pickupTask.getData().getOldWaybillCode())) {
            pickware.setWaybillCode(pickupTask.getData().getOldWaybillCode());
        }
        
        this.logger.info("取件单面单号：" + code + ", 商品详情：" + pickware.toString());
        
        return pickware;
        
    }
    
}
