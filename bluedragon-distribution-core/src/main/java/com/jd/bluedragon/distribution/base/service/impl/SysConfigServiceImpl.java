package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.dao.SysConfigDao;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("sysConfigService")
public class SysConfigServiceImpl implements SysConfigService {
    private static final Log logger= LogFactory.getLog(SysConfigServiceImpl.class);
    private static final String REDIS_LIMIT_SIZE_KEY="redisTaskQueueSize";
	@Autowired
	private SysConfigDao sysConfigDao;

	@Override
	public List<SysConfig> getSwitchList(){
		return this.sysConfigDao.getSwitchList();
	}

	@Override
	public List<SysConfig> getList(SysConfig sysConfig) {
		return this.sysConfigDao.getList(sysConfig);
	}


	/**
	 * 此接口仅用于查询sysconfig表的redis开关,其他接口禁止调用
     * fix 4/22 关闭redis缓存
	 * @param sysConfig
	 * @return
	 */
	@Override
	@Cache(key = "SysConfigServiceImpl.getRedisSwitchList@args0", memoryEnable = true, memoryExpiredTime = 5 * 1000, redisEnable = false)
	public List<SysConfig> getRedisSwitchList(String conName) {
		SysConfig config = new SysConfig();
		config.setConfigName(conName);
		return this.sysConfigDao.getList(config);
	}

	@Override
	@Cache(key = "SysConfigServiceImpl.findConfigContentByConfigName@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000
			,redisEnable = true, redisExpiredTime = 5 * 60 * 1000)
	public SysConfig findConfigContentByConfigName(String configName) {
		return this.sysConfigDao.findConfigContentByConfigName(configName);
	}



	@Override
	public int del(Long pk) {
		return this.sysConfigDao.del(pk);
	}

	@Override
	@Cache(key = "SysConfigServiceImpl.getCachedList@args0", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000
			,redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	public List<SysConfig> getCachedList(String conName) {
		return this.sysConfigDao.getListByConName(conName);
	}

    @Cache(key = "SysConfigServiceImpl.getMaxRedisQueueSize", memoryEnable = true, memoryExpiredTime = 60 * 1000,redisEnable = false)
    @Override
    public long getMaxRedisQueueSize() {
        long size=3000L;
        try {
            List<SysConfig> list=this.sysConfigDao.getListByConName(REDIS_LIMIT_SIZE_KEY);
            if(null!=list&&list.size()>0){
                SysConfig config=list.get(0);
                //NumberHelper.isNumber()
                size=Long.parseLong(config.getConfigContent());
            }
        }catch (Exception ex){
            logger.error("重新加载redisTaskQueueSize",ex);
        }
        return size;
    }
    /**
     * 通过配置名获取配置列表信息，并缓存10分钟
     */
    @Cache(key = "SysConfigServiceImpl.getListByConfigName@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,redisEnable = false)
	@Override
	public List<SysConfig> getListByConfigName(String configName) {
		SysConfig config = new SysConfig();
		config.setConfigName(configName);
		return this.sysConfigDao.getList(config);
	}

	/**
	 * 从sysconfig表里查出来内容为json格式的配置
	 * @return
	 */
	@Cache(key = "SiteServiceImpl.getSysConfigJsonContent@args0",memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,redisEnable = false)
	@Override
	public SysConfigContent getSysConfigJsonContent(String key){
		List<SysConfig> sysConfigs = getListByConfigName(key);
		if(sysConfigs != null && !sysConfigs.isEmpty()){
			return JsonHelper.fromJson(sysConfigs.get(0).getConfigContent(), SysConfigContent.class);
		}
		return null;
	}

	/**
	 * 获取开关值使用
	 * <p>
	 * 默认存1返回TRUE 0返回false
	 *
	 * 3 分钟缓存
	 * @param configName
	 * @return
	 */
	@Cache(key = "sysConfigService.getConfigByName@args0",memoryEnable = true, memoryExpiredTime = 3 * 60 * 1000,redisEnable = false)
	@Override
	public boolean getConfigByName(String configName) {
		try {
			List<com.jd.bluedragon.distribution.base.domain.SysConfig> sysConfigs = getListByConfigName(configName);
			if (null == sysConfigs || sysConfigs.size() <= 0) {
				return false;
			} else {
				if(sysConfigs.get(0).getConfigContent()==null){
					return false;
				}
				return sysConfigs.get(0).getConfigContent().equals("1");
			}
		} catch (Throwable ex) {
			return false;
		}

	}
}
