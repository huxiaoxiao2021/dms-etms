package com.jd.bluedragon.distribution.api.response;
/**
 * 
 * @ClassName: LoginUserRespnse
 * @Description: 登录账户信息返回实体
 * @author: wuyoude
 * @date: 2019年4月10日 上午10:39:26
 *
 */
public class LoginUserResponse extends BaseResponse{
	private static final long serialVersionUID = -7082200092556621496L;
	/**
	 * 登录人所在的分拣中心编码|绑定的分拣中心编码
	 */
	private Integer dmsSiteCode;
	/**
	 * 登录人所在的分拣中心名称|绑定的分拣中心名称
	 */
	private String dmsSiteName;
	/**
	 * 运行环境
	 */
	private String runningMode;
	/**
	 * @return the dmsSiteCode
	 */
	public Integer getDmsSiteCode() {
		return dmsSiteCode;
	}
	/**
	 * @param dmsSiteCode the dmsSiteCode to set
	 */
	public void setDmsSiteCode(Integer dmsSiteCode) {
		this.dmsSiteCode = dmsSiteCode;
	}
	/**
	 * @return the dmsSiteName
	 */
	public String getDmsSiteName() {
		return dmsSiteName;
	}
	/**
	 * @param dmsSiteName the dmsSiteName to set
	 */
	public void setDmsSiteName(String dmsSiteName) {
		this.dmsSiteName = dmsSiteName;
	}
	/**
	 * @return the runningMode
	 */
	public String getRunningMode() {
		return runningMode;
	}
	/**
	 * @param runningMode the runningMode to set
	 */
	public void setRunningMode(String runningMode) {
		this.runningMode = runningMode;
	}
}
