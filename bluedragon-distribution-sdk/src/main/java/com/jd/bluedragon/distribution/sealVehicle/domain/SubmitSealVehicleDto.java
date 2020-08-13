package com.jd.bluedragon.distribution.sealVehicle.domain;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SubmitSealVehicleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	* 封车任务编号
	* */
	private String sealVehicleTaskCode;

	/*
	* 运力编码
	* */
	private String transportCode;

	 /*
	 * 始发站点编号
	 * */
	private Integer createSiteCode;

	 /*
	 * 始发站点名称
	 * */
	private String createSiteName;

	 /*
	 * 目的站点编号
	 * */
	private Integer receiveSiteCode;

	 /*
	 * 目的站点名称
	 * */
	private String receiveSiteName;

	/*
	 * 操作时间
	 * */
	private Date operateTime;

	/*
	 * 操作人ERP
	 * */
	private String operatorErp;

	/*
	 * 操作人编号
	 * */
	private Integer operatorCode;

	/*
	 * 操作人编号姓名
	 * */
	private String operatorName;

    /**
     * 前台一键封车提交是否传递运力下的批次信息 true：包含 false：未包含
     *
     * <ul>
     *     <li>未就绪状态的任务，只有一辆车维护体积以后会提交批次</li>
     *     <li>未就绪状态的任务，有多辆车，批次选择车辆以后会提交批次</li>
     * </ul>
     */
    private Boolean hasBatchInfo;

    /*
    * 查询范围
    * */
    private Integer hourRange;

	/*
	* 封车批次信息
	* */
	private List<SealVehicleSendCodeInfo> sealVehicleSendCodeInfoList;


	public String getSealVehicleTaskCode() {
		return sealVehicleTaskCode;
	}

	public void setSealVehicleTaskCode(String sealVehicleTaskCode) {
		this.sealVehicleTaskCode = sealVehicleTaskCode;
	}

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public String getCreateSiteName() {
		return createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getReceiveSiteName() {
		return receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getOperatorErp() {
		return operatorErp;
	}

	public void setOperatorErp(String operatorErp) {
		this.operatorErp = operatorErp;
	}

	public Integer getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(Integer operatorCode) {
		this.operatorCode = operatorCode;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

    public Boolean getHasBatchInfo() {
        return hasBatchInfo;
    }

    public void setHasBatchInfo(Boolean hasBatchInfo) {
        this.hasBatchInfo = hasBatchInfo;
    }

	public Integer getHourRange() {
		return hourRange;
	}

	public void setHourRange(Integer hourRange) {
		this.hourRange = hourRange;
	}

	public List<SealVehicleSendCodeInfo> getSealVehicleSendCodeInfoList() {
		return sealVehicleSendCodeInfoList;
	}

	public void setSealVehicleSendCodeInfoList(List<SealVehicleSendCodeInfo> sealVehicleSendCodeInfoList) {
		this.sealVehicleSendCodeInfoList = sealVehicleSendCodeInfoList;
	}
}
