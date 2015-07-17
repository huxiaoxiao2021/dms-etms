package com.jd.bluedragon.distribution.api.response;

import java.util.ArrayList;

/**
 * @author yangpeng
 *
 */
public class RefundReason {
	/**
	 * 编码
	 */
	private Integer code;
	/**
	 * 名称
	 */
	private String name;
//	/**
//	 * 父编码
//	 */
//	private Integer parentCode;	
//	/**
//	 * 等级
//	 */
//	private Integer level;
	
	private ArrayList<RefundReason> childReason = new ArrayList<RefundReason>();
	
	public RefundReason(){
		
	}		
	
	public RefundReason(Integer code,String name/*,Integer parentCode,Integer level*/){
		this.code = code;
		this.name = name;
//		this.parentCode = parentCode;
//		this.level = level;
	}
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public Integer getParentCode() {
//		return parentCode;
//	}
//	public void setParentCode(Integer parentCode) {
//		this.parentCode = parentCode;
//	}
//	public Integer getLevel() {
//		return level;
//	}
//	public void setLevel(Integer level) {
//		this.level = level;
//	}
	public ArrayList<RefundReason> getChildReason() {
		return childReason;
	}
	public void setChildReson(ArrayList<RefundReason> childReason) {
		this.childReason = childReason;
	}
	public void addChild(Integer code,String name){
		RefundReason child = new RefundReason(code, name);
		childReason.add(child);
	}
}
