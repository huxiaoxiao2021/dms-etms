package com.jd.bluedragon.distribution.material.util;

import com.jd.bluedragon.distribution.material.service.MaterialOperationService;
import com.jd.bluedragon.distribution.material.service.MaterialSendService;
import com.jd.bluedragon.distribution.material.service.WarmBoxInOutOperationService;
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

    public enum MaterialSendModeEnum {

        MATERIAL_SINGLE_SEND((byte)1, "物资单个扫描发货"),

        MATERIAL_TYPE_BATCH_SEND((byte)2, "物资按类型批量发货");

        private byte code;

        private String name;

        MaterialSendModeEnum(byte code, String name) {
            this.code = code;
            this.name = name;
        }

        public byte getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    @Autowired
    @Qualifier("materialBatchSendService")
    private MaterialOperationService materialBatchSendService;

    @Autowired
    @Qualifier("warmBoxInOutOperationService")
    private MaterialOperationService warmBoxInOutOperationService;

    public MaterialOperationService findMaterialOperationService(Byte sendBusinessType){
        if (null == sendBusinessType) {
            throw new IllegalArgumentException("缺少必要参数!");
        }

        if (MaterialSendModeEnum.MATERIAL_SINGLE_SEND.code == sendBusinessType) {
            return warmBoxInOutOperationService;
        }
        else if (MaterialSendModeEnum.MATERIAL_TYPE_BATCH_SEND.code == sendBusinessType) {
            return materialBatchSendService;
        }

        return null;
    }
}
