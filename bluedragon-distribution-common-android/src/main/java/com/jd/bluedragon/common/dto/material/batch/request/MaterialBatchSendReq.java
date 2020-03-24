package com.jd.bluedragon.common.dto.material.batch.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName MaterialBatchSendReq
 * @Description
 * @Author wyh
 * @Date 2020/3/24 10:23
 **/
public class MaterialBatchSendReq implements Serializable {

    private static final long serialVersionUID = -7950275788413236585L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 发货批次号
     */
    private String sendCode;

    private Byte sendBusinessMode;

    /**
     * 发货明细
     */
    private List<MaterialSendByTypeDetailDto> sendDetails;

    public static class MaterialSendByTypeDetailDto implements Serializable {

        private static final long serialVersionUID = 2525231687138315203L;
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
