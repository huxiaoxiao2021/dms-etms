package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @ClassName: ClientRunningModeConfig
 * @Description: 客户端环境配置
 * @author: wuyoude
 * @date: 2019年7月10日 下午5:10:57
 *
 */
public class ClientRunningModeConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 默认环境
     */
    private String defaultRunningMode;
    /**
     * 启用机构列表
     */
    private List<ClientRunningModeConfigItem> configItems;
	/**
	 * @return the defaultRunningMode
	 */
	public String getDefaultRunningMode() {
		return defaultRunningMode;
	}

	/**
	 * @param defaultRunningMode the defaultRunningMode to set
	 */
	public void setDefaultRunningMode(String defaultRunningMode) {
		this.defaultRunningMode = defaultRunningMode;
	}

	/**
	 * @return the configItems
	 */
	public List<ClientRunningModeConfigItem> getConfigItems() {
		return configItems;
	}

	/**
	 * @param configItems the configItems to set
	 */
	public void setConfigItems(List<ClientRunningModeConfigItem> configItems) {
		this.configItems = configItems;
	}
	/**
	 * 
	 * @ClassName: ClientRunningModeConfigItem
	 * @Description: 配置明细
	 * @author: wuyoude
	 * @date: 2019年7月10日 下午9:13:49
	 *
	 */
    public static class ClientRunningModeConfigItem implements Serializable {

        private static final long serialVersionUID = 1L;
        /**
         * 环境设置
         */
        private String runningMode;
        /**
         * 启用机构列表
         */
        private List<Integer> orgCodes;
        /**
         * 启用站点列表
         */
        private List<Integer> siteCodes;
        /**
         * 启用用户erp列表
         */
        private List<String> userErps;
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
		/**
		 * @return the orgCodes
		 */
		public List<Integer> getOrgCodes() {
			return orgCodes;
		}
		/**
		 * @param orgCodes the orgCodes to set
		 */
		public void setOrgCodes(List<Integer> orgCodes) {
			this.orgCodes = orgCodes;
		}
		/**
		 * @return the siteCodes
		 */
		public List<Integer> getSiteCodes() {
			return siteCodes;
		}
		/**
		 * @param siteCodes the siteCodes to set
		 */
		public void setSiteCodes(List<Integer> siteCodes) {
			this.siteCodes = siteCodes;
		}
		/**
		 * @return the userErps
		 */
		public List<String> getUserErps() {
			return userErps;
		}
		/**
		 * @param userErps the userErps to set
		 */
		public void setUserErps(List<String> userErps) {
			this.userErps = userErps;
		}
    }
}
