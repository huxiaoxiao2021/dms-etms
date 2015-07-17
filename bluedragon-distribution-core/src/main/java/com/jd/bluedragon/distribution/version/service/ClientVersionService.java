package com.jd.bluedragon.distribution.version.service;

import java.util.List;

import com.jd.bluedragon.distribution.version.domain.ClientVersion;

public interface ClientVersionService {
	/**
	 * 查询所有的版本信息
	 * @return
	 */
	List<ClientVersion> getAll();
	
	/**
	 * 查询所有可用的版本信息
	 * @return
	 */
	List<ClientVersion> getAllAvailable();
	
	/**
	 * 依据ID查询版本信息
	 * @param id
	 * @return
	 */
	ClientVersion getById(Long id);
	
	/**
	 * 依据版本号查询版本信息
	 * @param versionCode
	 * @return
	 */
	List<ClientVersion> getByVersionCode(String versionCode);
	
	/**
	 * 依据版本类型查询所有的版本信息
	 * @param versionType
	 * @return
	 */
	List<ClientVersion> getByVersionType(Integer versionType);
	
	/**
	 * 是否存在版本信息
	 * @param clientVersion
	 * @return
	 */
	boolean exists(ClientVersion clientVersion);
	
	/**
	 * 添加版本信息
	 * @param clientVersion
	 * @return
	 */
	boolean add(ClientVersion clientVersion);
	
	/**
	 * 修改版本信息
	 * @param clientVersion
	 * @return
	 */
	boolean update(ClientVersion clientVersion);
	
	/**
	 * 修改版本信息
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
}
