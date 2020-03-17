package com.jd.bluedragon.distribution.api.request.material.warmbox;


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

    private String userErp;

    /**
     * 发货批次号
     */
    private String batchCode;

    private Byte sendBusinessType;

    /**
     * 发货明细
     */
    private List<MaterialSendDetail> sendDetails;

    // should be never changed
    private void setSendBusinessType(Byte sendBusinessType) {
        this.sendBusinessType = sendBusinessType;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Byte getSendBusinessType() {
        return sendBusinessType;
    }

    public List<MaterialSendDetail> getSendDetails() {
        return sendDetails;
    }

    public void setSendDetails(List<MaterialSendDetail> sendDetails) {
        this.sendDetails = sendDetails;
    }

    public class MaterialSendDetail implements Serializable {

        private static final long serialVersionUID = -3640063075948924774L;

        /**
         * 物资编码
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
