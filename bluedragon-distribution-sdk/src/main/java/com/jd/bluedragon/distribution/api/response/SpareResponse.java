package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class SpareResponse extends JdResponse {
    
    private static final long serialVersionUID = 6421643159029953636L;
    
    public static final Integer CODE_SPARE_NOT_FOUND = 23900;
    public static final String MESSAGE_SPARE_NOT_FOUND = "无备件条码信息";
    
    /** 全局唯一ID */
    private Long id;
    
    /** 创建时间 */
    private String createTime;
    
    /** 备件条码 */
    private String spareCode;
    
    /** 备件条码集合串 */
    private String spareCodes;
    
    private String type;
    
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public SpareResponse() {
        super();
    }
    
    public SpareResponse(Integer code, String message) {
        super(code, message);
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSpareCode() {
        return this.spareCode;
    }
    
    public void setSpareCode(String spareCode) {
        this.spareCode = spareCode;
    }
    
    public String getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public String getSpareCodes() {
        return this.spareCodes;
    }
    
    public void setSpareCodes(String spareCodes) {
        this.spareCodes = spareCodes;
    }
    
}
