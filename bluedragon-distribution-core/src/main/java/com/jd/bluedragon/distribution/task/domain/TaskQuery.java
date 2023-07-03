package com.jd.bluedragon.distribution.task.domain;

import java.util.Date;
import java.util.List;

public class TaskQuery implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
    /** 全局唯一ID */
    private Long id;
    
    /** 创建站点编号 */
    private Integer createSiteCode;
    
    /** 执行次数 */
    private Integer executeCount;
    
    /** 下次执行次数 */
    private Date executeTime;
    
    private Integer status;
    
    private String statuses;
    
    /** 关键词1 */
    private String keyword1;
    
    /** 关键词2 */
    private String keyword2;
    
    /** 动态表名 */
    private String tableName;
    
    /** 箱号 */
    private String boxCode;
    
    /** 收货单位Code */
    private Integer receiveSiteCode;
    
    /** 信息指纹 */
    private String fingerprint;
    
    /** 部署环境 */
    private String ownSign;

    /** 所属队列 */
    private Integer queueId;

    private List<Integer> queueIdList;    

    private List<Integer> statusList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getExecuteCount() {
		return executeCount;
	}

	public void setExecuteCount(Integer executeCount) {
		this.executeCount = executeCount;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatuses() {
		return statuses;
	}

	public void setStatuses(String statuses) {
		this.statuses = statuses;
	}

	public String getKeyword1() {
		return keyword1;
	}

	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}

	public String getKeyword2() {
		return keyword2;
	}

	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

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

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public String getOwnSign() {
		return ownSign;
	}

	public void setOwnSign(String ownSign) {
		this.ownSign = ownSign;
	}

	public Integer getQueueId() {
		return queueId;
	}

	public void setQueueId(Integer queueId) {
		this.queueId = queueId;
	}

	public List<Integer> getQueueIdList() {
		return queueIdList;
	}

	public void setQueueIdList(List<Integer> queueIdList) {
		this.queueIdList = queueIdList;
	}

	public List<Integer> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Integer> statusList) {
		this.statusList = statusList;
	}
    
}
