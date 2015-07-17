package com.jd.bluedragon.distribution.systemLog.domain;

import java.util.Date;


public class SystemLog implements Cloneable,java.io.Serializable{
	
	private static final long serialVersionUID = 5451064541238733135L;

	/** 全局唯一ID */
    private Long id;

    /** 关键字1 */
    private String keyword1;
    
    /** 关键字2 */
    private String keyword2;
    
    /** 关键字3 */
    private String keyword3;
    
    /** 关键字4 */
    private Long keyword4;
    
	/** 内容，按模块不同放入内容不同 */
	private String content;

    /** 日志类型 */
    private Long type;

    /** 创建时间 */
    private Date createTime;
    
    /** 可用否 */
    private Long yn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKeyword1() {
		return keyword1;
	}

	public void setKeyword1(String keyword1) {
        if(null!=keyword1&&keyword1.length()>32){
            this.keyword1 = keyword1.substring(0,32);
        }else {
            this.keyword1 = keyword1;
        }
	}


    public String getKeyword2() {
		return keyword2;
	}

	public void setKeyword2(String keyword2) {
        if(null!=keyword2&&keyword2.length()>32){
            this.keyword2 = keyword2.substring(0,32);
        }else {
            this.keyword2 = keyword2;
        }
	}

	public String getKeyword3() {
		return keyword3;
	}

	public void setKeyword3(String keyword3) {
        if(null!=keyword3&&keyword3.length()>64){
            this.keyword3 = keyword3.substring(0,64);
        }else {
            this.keyword3 = keyword3;
        }
	}

	public Long getKeyword4() {
		return keyword4;
	}

	public void setKeyword4(Long keyword4) {
		this.keyword4 = keyword4;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
        if(null!=content&&content.length()>4000){
            this.content = content.substring(0,4000);
        }else {
            this.content = content;
        }
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return this.createTime == null ? null : (Date) this.createTime.clone();
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime == null ? null : (Date) createTime.clone();
	}

	public Long getYn() {
		return yn;
	}

	public void setYn(Long yn) {
		this.yn = yn;
	}

	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}