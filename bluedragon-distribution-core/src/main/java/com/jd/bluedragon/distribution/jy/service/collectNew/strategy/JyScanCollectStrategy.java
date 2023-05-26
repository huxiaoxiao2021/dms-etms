package com.jd.bluedragon.distribution.jy.service.collectNew.strategy;

import com.jd.bluedragon.distribution.busineCode.jqCode.JQCodeService;
import com.jd.bluedragon.distribution.collection.service.JyScanCollectService;
import com.jd.bluedragon.distribution.jy.constants.JyPostEnum;
import com.jd.bluedragon.distribution.jy.constants.JyScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 21:02
 * @Description
 */
@Service
public class JyScanCollectStrategy {
    private Logger log = LoggerFactory.getLogger(JyWarehouseScanCollectHandler.class);

    @Autowired
    private JyScanCollectService jyScanCollectService;
    @Autowired
    private JQCodeService jqCodeService;

    public boolean scanCollectDeal(JyScanCollectDto collectDto) {
        String methodDesc = "JyScanCollectService.scanCollectDeal:拣运扫描处理集齐数据：";
        if (JyScanCodeTypeEnum.WAYBILL.getCode().equals(collectDto.getBarCodeType())) {
            return this.scanWaybillCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.PACKAGE.getCode().equals(collectDto.getBarCodeType())) {
            return this.scanPackageCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.BOX.getCode().equals(collectDto.getBarCodeType())) {
            return this.scanBoxCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.BOARD.getCode().equals(collectDto.getBarCodeType())) {
            return this.scanBoardCollectDeal(collectDto);
        }else {
            log.warn("{}目前仅支持处理按包裹、运单、箱号、板号维度处理集齐，当前类型暂不支持处理，直接丢弃，param={}", methodDesc, JsonHelper.toJson(collectDto));
            return true;
        }
    }


    public boolean scanWaybillCollectDeal(JyScanCollectDto collectDto) {
        return true;
    }

    public boolean scanPackageCollectDeal(JyScanCollectDto collectDto) {
        return true;
    }

    public boolean scanBoxCollectDeal(JyScanCollectDto collectDto) {
        return true;
    }

    public boolean scanBoardCollectDeal(JyScanCollectDto collectDto) {
        return true;
    }

    public String getCollectionCode(JyScanCollectDto collectDto) {
        String collectionCode = jqCodeService.getOrGenJyScanTaskCollectionCode(
                JyPostEnum.getJyPostEnumByCode(collectDto.getJyPostType()),
                collectDto.getOperateSiteId(),
                collectDto.getMainTaskBizId(),
                null);
        if(StringUtils.isBlank(collectionCode)) {
            if(log.isInfoEnabled()) {
                log.info("JyScanCollectStrategy.getCollectionCode获取为空，param={}", JsonHelper.toJson(collectDto));
            }
            throw new JyBizException("获取collectionCode为空");
        }
        return collectionCode;
    }



}
