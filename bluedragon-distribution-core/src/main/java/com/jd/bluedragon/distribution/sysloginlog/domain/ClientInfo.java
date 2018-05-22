package com.jd.bluedragon.distribution.sysloginlog.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端登录上传的信息
 * Created by shipeilin on 2018/1/16.
 */
public class ClientInfo {

    /** 操作人ERP */
    private String loginUserErp;
    /** 应用程序类型 */
    private Integer programType;

    /** 版本号:20180104WM */
    private String versionCode;

    /** 版本名称:BJ_MAJUQIAO */
    private String versionName;

    /** 文件版本号 */
    private String fileVersions;

    /** 和客户端配置是否匹配 */
    private Integer matchFlag;

    /** ipv4 */
    private String ipv4;

    /** ipv6 */
    private String ipv6;

    /** 主机名称 */
    private String machineName;

    /** mac地址 */
    private String macAdress;

    /** 文件版本列表 */
    private List<FileVersion> files;

    public static class FileVersion{
        private String filePath;
        private String versionCode;
        /**
         * @return the filePath
         */
        public String getFilePath() {
            return filePath;
        }
        /**
         * @param filePath the filePath to set
         */
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
        /**
         * @return the versionCode
         */
        public String getVersionCode() {
            return versionCode;
        }
        /**
         * @param versionCode the versionCode to set
         */
        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }

        @Override
        public String toString() {
            return filePath+":"+versionCode;
        }
    }

    public String getLoginUserErp() {
        return loginUserErp;
    }

    public void setLoginUserErp(String loginUserErp) {
        this.loginUserErp = loginUserErp;
    }

    /**
     * @return the programType
     */
    public Integer getProgramType() {
        return programType;
    }

    /**
     * @param programType the programType to set
     */
    public void setProgramType(Integer programType) {
        this.programType = programType;
    }

    /**
     * @return the versionCode
     */
    public String getVersionCode() {
        return versionCode;
    }

    /**
     * @param versionCode the versionCode to set
     */
    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * @return the versionName
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * @param versionName the versionName to set
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * @return the ipv4
     */
    public String getIpv4() {
        return ipv4;
    }

    /**
     * @param ipv4 the ipv4 to set
     */
    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    /**
     * @return the ipv6
     */
    public String getIpv6() {
        return ipv6;
    }

    /**
     * @param ipv6 the ipv6 to set
     */
    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }

    /**
     * @return the macAdress
     */
    public String getMacAdress() {
        return macAdress;
    }

    /**
     * @param macAdress the macAdress to set
     */
    public void setMacAdress(String macAdress) {
        this.macAdress = macAdress;
    }

    /**
     * @return the files
     */
    public List<FileVersion> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(List<FileVersion> files) {
        this.files = files;
    }

    /**
     * @return the machineName
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * @param machineName the machineName to set
     */
    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

	/**
	 * @return the matchFlag
	 */
	public Integer getMatchFlag() {
		return matchFlag;
	}

	/**
	 * @param matchFlag the matchFlag to set
	 */
	public void setMatchFlag(Integer matchFlag) {
		this.matchFlag = matchFlag;
	}
}
