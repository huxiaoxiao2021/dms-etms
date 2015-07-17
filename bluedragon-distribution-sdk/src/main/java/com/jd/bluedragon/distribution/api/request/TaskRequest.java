package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class TaskRequest extends JdRequest {

    private static final long serialVersionUID = 8900218370299464985L;

    /** 全局唯一ID */
    private Long taskId;

    /** 类型 */
    private Integer type;

    /** 关键词1 */
    private String keyword1;

    /** 关键词2 */
    private String keyword2;

    /** 数据内容 */
    private String body;
    
    /**箱号*/
    private String boxCode;
    
    /**收货单位Code*/
    private Integer receiveSiteCode;

    public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public Long getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getKeyword1() {
        return this.keyword1;
    }

    public void setKeyword1(String keyword1) {
        this.keyword1 = keyword1;
    }

    public String getKeyword2() {
        return this.keyword2;
    }

    public void setKeyword2(String keyword2) {
        this.keyword2 = keyword2;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
