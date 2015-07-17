package com.jd.bluedragon.distribution.seal.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.SealVehicleResponse;

public class SealVehicle {

	/** 全局唯一ID */
	private Long id;

	/** 封车编号 */
	private String code;

	/** 车辆编号 */
	private String vehicleCode;

	/** 司机编号 */
	private String driverCode;

	/** 司机姓名 */
	private String driver;

	/** 发货批次号 */
	private String sendCode;

	/** 重量 */
	private Double weight;

	/** 体积 */
	private Double volume;

	/** 件数 */
	private Integer packageNum;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SealVehicle other = (SealVehicle) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 创建站点名称 */
	private String createSiteName;

	/** 接收站点编号 */
	private Integer receiveSiteCode;

	/** 接受站点名称 */
	private String receiveSiteName;

	/** 创建人编号 */
	private Integer createUserCode;

	/** 创建人 */
	private String createUser;

	/** 创建时间 */
	private Date createTime;

	/** 最后操作人编号 */
	private Integer updateUserCode;

	/** 最后操作人 */
	private String updateUser;

	/** 最后修改时间 */
	private Date updateTime;

	/** 是否删除 '0' 删除 '1' 使用 */
	private Integer yn;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVehicleCode() {
		return this.vehicleCode;
	}

	public void setVehicleCode(String vehicleCode) {
		this.vehicleCode = vehicleCode;
	}

	public String getDriverCode() {
		return this.driverCode;
	}

	public void setDriverCode(String driverCode) {
		this.driverCode = driverCode;
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Integer getCreateSiteCode() {
		return this.createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public String getCreateSiteName() {
		return this.createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public Integer getReceiveSiteCode() {
		return this.receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getReceiveSiteName() {
		return this.receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	public Integer getCreateUserCode() {
		return this.createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime != null ? (Date) createTime.clone() : null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime != null ? (Date) createTime.clone() : null;
	}

	public Integer getUpdateUserCode() {
		return this.updateUserCode;
	}

	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	public String getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime != null ? (Date) updateTime.clone() : null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime != null ? (Date) updateTime.clone() : null;
	}

	public Integer getYn() {
		return this.yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Integer getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(Integer packageNum) {
		this.packageNum = packageNum;
	}

	public static SealVehicle toSealVehicle(SealVehicleRequest request) {
		SealVehicle sealVehicle = new SealVehicle();
		sealVehicle.setCreateSiteCode(request.getSiteCode());
		sealVehicle.setCreateSiteName(request.getSiteName());
		sealVehicle.setCreateUser(request.getUserName());
		sealVehicle.setCreateUserCode(request.getUserCode());
		sealVehicle.setCode(request.getSealCode());
		sealVehicle.setVehicleCode(request.getVehicleCode());
		sealVehicle.setDriver(request.getDriver());
		if (request.getDriverCode() != null) {
			sealVehicle.setDriverCode(String.valueOf(request.getDriverCode()));
		}
		return sealVehicle;
	}

	public static SealVehicleResponse toSealVehicleResponse(SealVehicle sealVehicle) {
		SealVehicleResponse response = new SealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setId(sealVehicle.getId());
		response.setVehicleCode(sealVehicle.getVehicleCode());
		response.setSealCode(sealVehicle.getCode());
		response.setDriver(sealVehicle.getDriver());
		try {
			response.setDriverCode(Integer.valueOf(sealVehicle.getDriverCode()));
		} catch (Exception e) {
			response.setDriverCode(0);
		}
		return response;
	}

	public static SealVehicle toSealVehicle2(SealVehicleRequest request) {
		SealVehicle sealVehicle = new SealVehicle();
		sealVehicle.setCode(request.getSealCode());
		sealVehicle.setVehicleCode(request.getVehicleCode());
		sealVehicle.setReceiveSiteCode(request.getSiteCode());
		sealVehicle.setReceiveSiteName(request.getSiteName());
		sealVehicle.setUpdateUser(request.getUserName());
		sealVehicle.setUpdateUserCode(request.getUserCode());
		return sealVehicle;
	}

	public static SealVehicle toSealVehicle3(SealVehicleRequest request) {
		SealVehicle sealVehicle = new SealVehicle();
		sealVehicle.setCreateSiteCode(request.getSiteCode());
		sealVehicle.setCreateSiteName(request.getSiteName());
		sealVehicle.setCreateUser(request.getUserName());
		sealVehicle.setCreateUserCode(request.getUserCode());
		sealVehicle.setCode(request.getSealCode());
		sealVehicle.setVehicleCode(request.getVehicleCode());
		sealVehicle.setDriver(request.getDriver());
		if (request.getDriverCode() != null) {
			sealVehicle.setDriverCode(String.valueOf(request.getDriverCode()));
		}
		sealVehicle.setSendCode(request.getSendCode());
		sealVehicle.setWeight(request.getWeight());
		sealVehicle.setVolume(request.getVolume());
		sealVehicle.setPackageNum(request.getPackageNum());
		return sealVehicle;
	}

	public static SealVehicle toSealVehicle4(SealVehicleRequest request) {
		SealVehicle sealVehicle = new SealVehicle();
		sealVehicle.setCode(request.getSealCode());
		sealVehicle.setVehicleCode(request.getVehicleCode());
		sealVehicle.setReceiveSiteCode(request.getSiteCode());
		sealVehicle.setReceiveSiteName(request.getSiteName());
		sealVehicle.setUpdateUser(request.getUserName());
		sealVehicle.setUpdateUserCode(request.getUserCode());
		sealVehicle.setSendCode(request.getSendCode()); // 注入批次号
		return sealVehicle;
	}

	// sendcodelist和sealcodelist的长度不固定,以最长的作为基准,另一个随机重复
	public static List<SealVehicle> toSomeSealVehicle(SealVehicleRequest request) {
		List<SealVehicle> sealVehicleList = new ArrayList<SealVehicle>();
		List<String> sealCodeList = request.getSealCodeList();
		List<String> sendCodeList = request.getSendCodeList();
		int sealCodeSize = sealCodeList.size();
		int sendCodeSize = sendCodeList.size();
		if (sealCodeSize == sendCodeSize) { // 正好完整匹配
			for (int i = 0; i < sealCodeSize; i++) {
				SealVehicle sealVehicle = getSingleSealVehicle(request);
				sealVehicle.setCode(sealCodeList.get(i));
				sealVehicle.setSendCode(sendCodeList.get(i)); // 注入批次号
				sealVehicleList.add(sealVehicle);
			}
			return sealVehicleList;
		} else if (sealCodeSize > sendCodeSize) {
			for (int i = 0; i < sealCodeSize; i++) {
				int j = i + 1;
				if (j <= sendCodeSize) { // 一对一匹配
					SealVehicle sealVehicle = getSingleSealVehicle(request);
					sealVehicle.setCode(sealCodeList.get(i));
					sealVehicle.setSendCode(sendCodeList.get(i)); // 注入批次号
					sealVehicleList.add(sealVehicle);
				} else { // sendCode做重复匹配
					SealVehicle sealVehicle = getSingleSealVehicle(request);
					sealVehicle.setCode(sealCodeList.get(i));
					sealVehicle.setSendCode(sendCodeList.get(0)); // 注入批次号
					sealVehicleList.add(sealVehicle);
				}
			}
			return sealVehicleList;
		} else {
			for (int i = 0; i < sendCodeSize; i++) {
				int j = i + 1;
				if (j <= sealCodeSize) { // 一对一匹配
					SealVehicle sealVehicle = getSingleSealVehicle(request);
					sealVehicle.setCode(sealCodeList.get(i));
					sealVehicle.setSendCode(sendCodeList.get(i)); // 注入批次号
					sealVehicleList.add(sealVehicle);
				} else { // sendCode做重复匹配
					SealVehicle sealVehicle = getSingleSealVehicle(request);
					sealVehicle.setCode(sealCodeList.get(0));
					sealVehicle.setSendCode(sendCodeList.get(i)); // 注入批次号
					sealVehicleList.add(sealVehicle);
				}
			}
			return sealVehicleList;
		}
	}

	public static SealVehicle getSingleSealVehicle(SealVehicleRequest request) {
		SealVehicle sealVehicle = new SealVehicle();
		sealVehicle.setVehicleCode(request.getVehicleCode());   
		sealVehicle.setCreateSiteCode(request.getSiteCode());  
		sealVehicle.setCreateSiteName(request.getSiteName());
		sealVehicle.setCreateUser(request.getUserName());
		sealVehicle.setCreateUserCode(request.getUserCode());
		sealVehicle.setVolume(request.getVolume());
		sealVehicle.setWeight(request.getWeight());
		sealVehicle.setPackageNum(request.getPackageNum());
		sealVehicle.setDriver(request.getDriver());
		if (request.getDriverCode() != null) {
			sealVehicle.setDriverCode(String.valueOf(request.getDriverCode()));
		}
		return sealVehicle;
	}

	public static void main(String[] args) {
		SealVehicleRequest request = new SealVehicleRequest();
		request.setSealCodes("seal3,seal4,seal2,seal1");
		request.setSendCodes("send2,send3,send1,");
		List<SealVehicle> sealVehicleList = toSomeSealVehicle(request);
		System.out.println(sealVehicleList.size());
		for (SealVehicle sc : sealVehicleList) {
			System.out.println("sendCode : " + sc.getSendCode() + " , sealCode : " + sc.getCode());
		}
	}
}