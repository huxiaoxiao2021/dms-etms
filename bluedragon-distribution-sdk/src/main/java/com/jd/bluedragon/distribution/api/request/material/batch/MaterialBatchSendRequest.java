package com.jd.bluedragon.distribution.api.request.material.batch;


import com.jd.bluedragon.distribution.api.JdRequest;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName MaterialBatchSendRequest
 * @Description
 * @Author wyh
 * @Date 2020/3/16 16:19
 **/
public class MaterialBatchSendRequest extends JdRequest {

    private static final long serialVersionUID = -2483299650913701153L;

    public MaterialBatchSendRequest() {}

    public MaterialBatchSendRequest(Byte sendBusinessType) {
        this.sendBusinessMode = sendBusinessType;
    }

    private String userErp;

    /**
     * 发货批次号
     */
    private String sendCode;

    private Byte sendBusinessMode;

    /**
     * 发货明细
     */
    private List<MaterialSendByTypeDetail> sendDetails;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    // should be never changed
    private void setSendBusinessMode(Byte sendBusinessMode) {
        this.sendBusinessMode = sendBusinessMode;
    }

    public Byte getSendBusinessMode() {
        return sendBusinessMode;
    }

    public List<MaterialSendByTypeDetail> getSendDetails() {
        return sendDetails;
    }

    public void setSendDetails(List<MaterialSendByTypeDetail> sendDetails) {
        this.sendDetails = sendDetails;
    }

    public static class MaterialSendByTypeDetail implements Serializable {

        private static final long serialVersionUID = -3640063075948924774L;

        /**
         * 物资类型编码
         */
        private String materialTypeCode;

        /**
         * 物资名称
         */
        private String materialName;

        /**
         * 发货数量
         */
        private Integer sendNum;

        public String getMaterialTypeCode() {
            return materialTypeCode;
        }

        public void setMaterialTypeCode(String materialTypeCode) {
            this.materialTypeCode = materialTypeCode;
        }

        public void setMaterialName(String materialName) {
            this.materialName = materialName;
        }

        public void setSendNum(Integer sendNum) {
            this.sendNum = sendNum;
        }

        public String getMaterialName() {
            return materialName;
        }

        public Integer getSendNum() {
            return sendNum;
        }
    }
}
