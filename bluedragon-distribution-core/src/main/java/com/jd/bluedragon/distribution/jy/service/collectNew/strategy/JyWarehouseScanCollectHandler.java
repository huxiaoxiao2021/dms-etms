package com.jd.bluedragon.distribution.jy.service.collectNew.strategy;

import com.jd.bluedragon.distribution.jy.constants.JyPostEnum;
import com.jd.bluedragon.distribution.jy.constants.JyScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.service.collectNew.factory.JyScanCollectStrategyFactory;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 *
 * @Author zhengchengfa
 * @Date 2023/5/25 21:12
 * @Description
 */
@Service
public class JyWarehouseScanCollectHandler extends JyScanCollectStrategy implements InitializingBean {
    private Logger log = LoggerFactory.getLogger(JyWarehouseScanCollectHandler.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        JyScanCollectStrategyFactory.registerJyScanCollectService(JyPostEnum.SEND_SEAL_WAREHOUSE.getCode(), this);
    }

    @Override
    public boolean scanCollectDeal(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyWarehouseScanCollectService.scanCollectDeal:接货仓发货岗扫描处理集齐数据：";
        if (JyScanCodeTypeEnum.WAYBILL.getCode().equals(collectDto.getBarCodeType())) {
            return super.scanWaybillCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.PACKAGE.getCode().equals(collectDto.getBarCodeType())) {
            return super.scanPackageCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.BOX.getCode().equals(collectDto.getBarCodeType())) {
            return super.scanBoxCollectDeal(collectDto);
        } else {
            log.warn("{}目前仅支持处理按包裹、运单、箱号维度处理集齐，当前类型暂不支持处理，直接丢弃，param={}", methodDesc, JsonHelper.toJson(collectDto));
            return true;
        }
    }
}
