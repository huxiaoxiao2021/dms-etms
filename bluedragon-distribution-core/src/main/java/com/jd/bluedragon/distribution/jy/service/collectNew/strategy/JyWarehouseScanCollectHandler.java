package com.jd.bluedragon.distribution.jy.service.collectNew.strategy;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.collectNew.service.JyScanCollectService;
import com.jd.bluedragon.distribution.jy.constants.JyCollectScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyCancelScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.service.collectNew.factory.JyScanCollectStrategyFactory;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 *
 * @Author zhengchengfa
 * @Date 2023/5/25 21:12
 * @Description
 */
@Service
public class JyWarehouseScanCollectHandler extends JyScanCollectStrategy implements InitializingBean {
    private Logger log = LoggerFactory.getLogger(JyWarehouseScanCollectHandler.class);


    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private JyScanCollectService jyScanCollectService;

    @Override
    public void afterPropertiesSet() throws Exception {
        JyScanCollectStrategyFactory.registerJyScanCollectService(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode(), this);
    }

    @Override
    public boolean scanCollectDeal(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyWarehouseScanCollectService.scanCollectDeal:接货仓发货岗扫描处理集齐数据：";
        log.info("{}param={}", methodDesc, JsonHelper.toJson(collectDto));
        if(!this.filterInvalid(collectDto)) {
            return true;
        }

        //必要字段前置加工
        this.mustFieldFill(collectDto);

        if (JyCollectScanCodeTypeEnum.WAYBILL.getCode().equals(collectDto.getCodeType())) {
            return super.scanWaybillCollectDeal(collectDto);
        } else if (JyCollectScanCodeTypeEnum.PACKAGE.getCode().equals(collectDto.getCodeType())) {
            return super.scanPackageCollectDeal(collectDto);
        } else if (JyCollectScanCodeTypeEnum.BOX.getCode().equals(collectDto.getCodeType())) {
            return super.scanBoxCollectDeal(collectDto);
        } else {
            log.warn("{}目前仅支持处理按包裹、运单、箱号维度处理集齐，当前类型暂不支持处理，直接丢弃，param={}", methodDesc, JsonHelper.toJson(collectDto));
            return true;
        }
    }

    /**
     * 过滤无效数据
     * @param collectDto
     * @return true: 有效数据  false: 无效数据
     */
    public boolean filterInvalid(JyScanCollectMqDto collectDto) {
        if(!super.scanFilterInvalid(collectDto)) {
            log.warn("JyWarehouseScanCollectHandler.filterInvalid:接货仓发货集齐消息消费必要参数缺失1，不做处理，msg={}", JsonHelper.toJson(collectDto));
            return false;
        }
        if(StringUtils.isBlank(collectDto.getSendCode())    //接货仓发货岗是批次维度，必传批次号
        ) {
            log.warn("JyWarehouseScanCollectHandler.filterInvalid:接货仓发货集齐消息消费必要参数缺失2，不做处理，msg={}", JsonHelper.toJson(collectDto));
            return false;
        }
        return true;
    }

    @Override
    public boolean cancelScanCollectDeal(JyCancelScanCollectMqDto jyCancelScanCollectMqDto){
        return super.cancelScanCollectDeal(jyCancelScanCollectMqDto);
    }


    @Override
    public void mustFieldFill(JyScanCollectMqDto collectDto) {
        //扫描类型getBarCodeType是运单，或者扫描任何维度最终类型getCodeType转为包裹维度（扫包、扫单拆包、扫箱拆包）  补齐运单属性
        if( (JyCollectScanCodeTypeEnum.PACKAGE.getCode().equals(collectDto.getCodeType())
                || JyCollectScanCodeTypeEnum.WAYBILL.getCode().equals(collectDto.getBarCodeType()))
            &&
                (StringUtils.isBlank(collectDto.getToBWaybill())) || Objects.isNull(collectDto.getWaybillGoodNumber()))
        {
            //按包裹扫描需要补充
            Waybill waybill = waybillQueryManager.getWaybillByWayCode(collectDto.getWaybillCode());
            String wbs = Objects.isNull(waybill) ? "" : waybill.getWaybillSign();
            Integer goodNumber = (Objects.isNull(waybill) || Objects.isNull(waybill.getGoodNumber())) ? 0 : waybill.getGoodNumber();

            collectDto.setToBWaybill(jyScanCollectService.toBNetFlag(collectDto.getWaybillCode(), wbs));
            collectDto.setWaybillGoodNumber(goodNumber);
        }
    }
}
