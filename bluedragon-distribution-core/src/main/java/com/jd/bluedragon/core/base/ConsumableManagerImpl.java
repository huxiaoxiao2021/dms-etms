package com.jd.bluedragon.core.base;

import erp.ql.station.api.dto.CommonResponseDto;
import erp.ql.station.api.dto.WrapBillManageDto;
import erp.ql.station.api.service.wrap.WrapCategoryAndWrapBillInfoApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ConsumableManagerImpl implements ConsumableManager{

    @Autowired
    private WrapCategoryAndWrapBillInfoApi wrapCategoryAndWrapBillInfoApi;

    private static final Integer EXPRESS_DELIVERY = 1;

    @Override
    public Boolean checkConsumable(List<String> consumableBarcodes, String Erp) {
        CommonResponseDto<List<WrapBillManageDto>> listCommonResponseDto;
        try {
            listCommonResponseDto = wrapCategoryAndWrapBillInfoApi.queryWrapPrices(EXPRESS_DELIVERY, consumableBarcodes, Erp);
            if (CommonResponseDto.CODE_SUCCESS != listCommonResponseDto.getCode()) {
                return false;
            }
            if (CommonResponseDto.CODE_SUCCESS == listCommonResponseDto.getCode()) {
                for (WrapBillManageDto dto : listCommonResponseDto.getData()) {
                    if (StringUtils.isEmpty(dto.getPrice())) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("获取终端耗材条码出错:{}", e);
            return false;
        }
    }
}
