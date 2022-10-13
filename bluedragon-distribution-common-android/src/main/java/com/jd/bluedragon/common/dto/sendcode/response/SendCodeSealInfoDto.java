package com.jd.bluedragon.common.dto.sendcode.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : wuyoude
 * @date : 2022/10/12
 */
public class SendCodeSealInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 始发站点code
     */
    private Integer createSiteCode;

    /**
     * 始发站点名称
     */
    private String createSiteName;

    /**
     * 目的站点的code
     */
    private Integer receiveSiteCode;

    /**
     * 目的站点的名称
     */
    private String receiveSiteName;
    /**
     * 封车状态
     */
    private Integer sealStatusCode;
    /**
     * 封车状态名称
     */
    private String sealStatusName;
    
    private Date sealTime;
    
    private String sealUserErp;
    
    private Integer scanPackageNum = 0;
    
    private Integer scanBoxNum = 0;
    
    private Integer scanBoardNum = 0;

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

	public Integer getSealStatusCode() {
		return sealStatusCode;
	}

	public void setSealStatusCode(Integer sealStatusCode) {
		this.sealStatusCode = sealStatusCode;
	}

	public String getSealStatusName() {
		return sealStatusName;
	}

	public void setSealStatusName(String sealStatusName) {
		this.sealStatusName = sealStatusName;
	}

	public Date getSealTime() {
		return sealTime;
	}

	public void setSealTime(Date sealTime) {
		this.sealTime = sealTime;
	}

	public String getSealUserErp() {
		return sealUserErp;
	}

	public void setSealUserErp(String sealUserErp) {
		this.sealUserErp = sealUserErp;
	}

	public Integer getScanPackageNum() {
		return scanPackageNum;
	}

	public void setScanPackageNum(Integer scanPackageNum) {
		this.scanPackageNum = scanPackageNum;
	}

	public Integer getScanBoxNum() {
		return scanBoxNum;
	}

	public void setScanBoxNum(Integer scanBoxNum) {
		this.scanBoxNum = scanBoxNum;
	}

	public Integer getScanBoardNum() {
		return scanBoardNum;
	}

	public void setScanBoardNum(Integer scanBoardNum) {
		this.scanBoardNum = scanBoardNum;
	}
    
}
