package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.dao.SysConfigDao;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
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
	@Cache(key = "Test1SysConfigServiceImpl.findConfigContentByConfigName@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000
			,redisEnable = true, redisExpiredTime = 5 * 60 * 1000)
	public SysConfig findConfigContentByConfigName(String configName) {
		SysConfig result=this.sysConfigDao.findConfigContentByConfigName(configName);
		return result;
		//return this.sysConfigDao.findConfigContentByConfigName(configName);
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
}
