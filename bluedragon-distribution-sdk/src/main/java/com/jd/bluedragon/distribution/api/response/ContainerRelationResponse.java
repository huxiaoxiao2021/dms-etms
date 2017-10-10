package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * Created by lhc on 2017/5/4.
 */
public class ContainerRelationResponse extends JdResponse {
	
	private static final long serialVersionUID = 6421643159029953636L;

	/**
	 * 主键
	 */
	private String id;
 	
	/**
	 * 编码
	 */
	private String containerCode;
 	
	/**
	 * 箱号
	 */
	private String boxCode;
 	
	/**
	 * 储位对应的站点
	 */
	private Integer siteCode;
	
	/**
	 * 箱号对应的目的站点
	 */
	private Integer receiveSiteCode;
 	
	/**
	 * 包裹个数
	 */
	private String packageCount;
	
	/**
	 * 状态
	 */
	private Integer status;
	
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	public ContainerRelationResponse(Integer code, String message){
		super(code,message);
	}
	
	/**
	 * @return the containerCode
	 */
	public String getContainerCode() {
		return containerCode;
	}
	
	/**
	 * @param containerCode the containerCode to set
	 */
	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}
	
	
	
	
	/**
	 * @return the boxCode
	 */
	public String getBoxCode() {
		return boxCode;
	}
	
	/**
	 * @param boxCode the boxCode to set
	 */
	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}
	
	
	
	
	/**
	 * @return the siteCode
	 */
	public Integer getSiteCode() {
		return siteCode;
	}
	
	/**
	 * @param siteCode the siteCode to set
	 */
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}
	
	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}
	
	/**
	 * @return the packageCount
	 */
	public String getPackageCount() {
		return packageCount;
	}
	
	/**
	 * @param packageCount the packageCount to set
	 */
	public void setPackageCount(String packageCount) {
		this.packageCount = packageCount;
	}
	
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
