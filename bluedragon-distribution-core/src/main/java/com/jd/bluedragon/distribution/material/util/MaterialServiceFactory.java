package com.jd.bluedragon.distribution.material.util;

import com.jd.bluedragon.distribution.material.service.MaterialSendService;
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

    public enum SendBusinessTypeEnum {

        WARM_BOX_SEND((byte)1, "保温箱出库"),

        BATCH_SEND((byte)2, "物资按批次号发货");

        private byte code;

        private String name;

        SendBusinessTypeEnum(byte code, String name) {
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
    private MaterialSendService materialBatchSendService;

    @Autowired
    @Qualifier("warmBoxInOutOperationService")
    private MaterialSendService warmBoxInOutOperationService;

    public MaterialSendService getMaterialSendService(Byte sendBusinessType){
        if (null == sendBusinessType) {
            throw new IllegalArgumentException("缺少必要参数!");
        }

        if (SendBusinessTypeEnum.WARM_BOX_SEND.code == sendBusinessType) {
            return warmBoxInOutOperationService;
        }
        else if (SendBusinessTypeEnum.BATCH_SEND.code == sendBusinessType) {
            return materialBatchSendService;
        }

        return null;
    }
}
