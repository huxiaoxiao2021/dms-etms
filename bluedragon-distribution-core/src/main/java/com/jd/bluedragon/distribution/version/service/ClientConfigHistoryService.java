package com.jd.bluedragon.distribution.version.service;

import java.util.List;

import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;

public interface ClientConfigHistoryService {
	/**
	 * 查询所有的配置变更历史信息
	 * @return
	 */
	List<ClientConfigHistory> getAll(); 
	
	/**
	 * 依据分拣中心编号查询配置变更历史信息
	 * @param siteCode
	 * @return
	 */
	List<ClientConfigHistory> getBySiteCode(String siteCode);
	
	/**
	 * 依据应用程序类型查询所有的配置变更历史信息
	 * @param programType
	 * @return
	 */
	List<ClientConfigHistory> getByProgramType(Integer programType);
 
}
