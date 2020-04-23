package com.jd.bluedragon.distribution.rule.domain;

import com.jd.bluedragon.distribution.api.JdResponse;

public class RuleResponse extends JdResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3625871447911666844L;
	
    private Integer type;
    private String content;
    private String inOut;
    
    public RuleResponse() {
    	
    }
    
    public RuleResponse(Integer code, String message) {
        super(code, message);
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInOut() {
        return this.inOut;
    }

    public void setInOut(String inOut) {
        this.inOut = inOut;
    }
}
