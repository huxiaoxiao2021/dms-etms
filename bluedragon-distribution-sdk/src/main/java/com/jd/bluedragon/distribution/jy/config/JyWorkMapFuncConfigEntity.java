package com.jd.bluedragon.distribution.jy.config;


import java.io.Serializable;
import java.util.Date;

/**
 * 拣运APP功能和工序映射表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-02 17:50:10
 */
public class JyWorkMapFuncConfigEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * ref：work_station业务主键
	 */
	private String refWorkKey;
	/**
	 * 功能编码
	 */
	private String funcCode;
	/**
	 * 创建人ERP
	 */
	private String createUser;
	/**
	 * 更新人ERP
	 */
	private String updateUser;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 逻辑删除标志,0-删除,1-正常
	 */
	private Integer yn;
	/**
	 * 数据库时间
	 */
	private Date ts;

	public Long setId(Long id){
		return this.id = id;
	}

		public Long getId(){
		return this.id;
	}
	public String setRefWorkKey(String refWorkKey){
		return this.refWorkKey = refWorkKey;
	}

		public String getRefWorkKey(){
		return this.refWorkKey;
	}
	public String setFuncCode(String funcCode){
		return this.funcCode = funcCode;
	}

		public String getFuncCode(){
		return this.funcCode;
	}
	public String setCreateUser(String createUser){
		return this.createUser = createUser;
	}

		public String getCreateUser(){
		return this.createUser;
	}
	public String setUpdateUser(String updateUser){
		return this.updateUser = updateUser;
	}

		public String getUpdateUser(){
		return this.updateUser;
	}
	public Date setCreateTime(Date createTime){
		return this.createTime = createTime;
	}

		public Date getCreateTime(){
		return this.createTime;
	}
	public Date setUpdateTime(Date updateTime){
		return this.updateTime = updateTime;
	}

		public Date getUpdateTime(){
		return this.updateTime;
	}
	public Integer setYn(Integer yn){
		return this.yn = yn;
	}

		public Integer getYn(){
		return this.yn;
	}
	public Date setTs(Date ts){
		return this.ts = ts;
	}

		public Date getTs(){
		return this.ts;
	}

}
