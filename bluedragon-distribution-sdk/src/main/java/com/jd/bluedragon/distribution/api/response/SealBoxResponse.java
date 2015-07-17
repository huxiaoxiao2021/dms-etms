package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class SealBoxResponse extends JdResponse {

    private static final long serialVersionUID = -9096768337471427500L;

    public static final Integer CODE_SEAL_BOX_NOT_FOUND = 20101;
    public static final String MESSAGE_SEAL_BOX_NOT_FOUND = "无封箱信息";

    /** 全局唯一ID */
    private Long id;

    /** 封箱编号 */
    private String sealCode;

    /** 箱号 */
    private String boxCode;

    public SealBoxResponse() {
        super();
    }

    public SealBoxResponse(Integer code, String message) {
        super(code, message);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSealCode() {
        return this.sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public String getBoxCode() {
        return this.boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

}
