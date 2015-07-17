package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class TaskResponse extends JdResponse {

    private static final long serialVersionUID = 6421643159029953636L;

    /** 全局唯一ID */
    private Long id;

    /** 创建时间 */
    private String createTime;
    
    public TaskResponse(Integer code, String message, String createTime) {
    	super(code, message);
        this.createTime = createTime;
    }

    public TaskResponse(Integer code, String message) {
        super(code, message);
    }

    public TaskResponse() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

}
