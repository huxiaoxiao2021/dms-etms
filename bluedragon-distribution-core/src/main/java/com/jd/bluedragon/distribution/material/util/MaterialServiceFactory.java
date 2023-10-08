package com.jd.bluedragon.distribution.material.util;

import com.jd.bluedragon.distribution.external.intensive.enums.MaterialSendModeEnum;
import com.jd.bluedragon.distribution.material.service.MaterialOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @ClassName MaterialServiceFactory
 * @Description
 * @Author wyh
 * @Date 2020/3/16 18:16
 **/
@Component
public class MaterialServiceFactory {

    @Autowired
    @Qualifier("materialBatchSendService")
    private MaterialOperationService materialBatchSendService;

    @Autowired
    @Qualifier("warmBoxInOutOperationService")
    private MaterialOperationService warmBoxInOutOperationService;

    @Autowired
    @Qualifier("collectionBagOperationService")
    private MaterialOperationService collectionBagOperationService;

    @Autowired
    @Qualifier("recyclingBoxInOutOperationService")
    private MaterialOperationService recyclingBoxInOutOperationService;

    public MaterialOperationService findMaterialOperationService(Byte sendBusinessType){
        if (null == sendBusinessType) {
            throw new IllegalArgumentException("缺少必要参数!");
        }

        if (MaterialSendModeEnum.WARM_BOX_SEND.getCode() == sendBusinessType) {
            return warmBoxInOutOperationService;
        }
        else if (MaterialSendModeEnum.TYPE_BATCH_SEND.getCode() == sendBusinessType) {
            return materialBatchSendService;
        }
        else if (MaterialSendModeEnum.MATERIAL_TAG_SEND.getCode() == sendBusinessType) {
            return recyclingBoxInOutOperationService;
        }
        else if (MaterialSendModeEnum.COLLECTION_BAG_SEND.getCode() == sendBusinessType) {
            return collectionBagOperationService;
        }

        return null;
    }
}
