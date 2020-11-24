package com.jd.bluedragon.distribution.systemLog.service.impl;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.systemLog.dao.SystemLogDao;
import com.jd.bluedragon.distribution.systemLog.dao.SystemlogCassandra;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.systemLog.service.SystemLogService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;



/**
 *
 * 不要使用此接口保存日志了。请使用统一的日志日志接口com.jd.bluedragon.distribution.log.impl.LogEngineImpl。
 * com.jd.bluedragon.distribution.log.impl.LogEngineImpl 此接口保存的日志会存储到business.log.jd.com 中;
 *
 *
 * 系统日志记录工具
 * @author huangliang
 *
 */
@Service("SystemLogService")
@Deprecated
public class SystemLogServiceImpl implements SystemLogService {

	private final static Logger log = LoggerFactory.getLogger(SystemLogServiceImpl.class);

	@Autowired
	private SystemLogDao systemLogDao;
	
	@Autowired
	private SystemlogCassandra logCassandra;
	
	@Autowired
	private BaseService baseService;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;
	
	//cassandra开关
	public static final String CASSANDRA_SWITCH = "CASSANDRA_SWITCH";

	public int add(SystemLog systemLog) {

	    if (!uccPropertyConfiguration.isSystemLogGlobalSwitch()) {
	        return 0;
        }

		if(StringUtils.isBlank(systemLog.getContent())){
			systemLog.setContent("content 为空!");
		}
		try {
			byte[] contentBytes = systemLog.getContent().getBytes();
			if(contentBytes.length>4000) systemLog.setContent(new String(contentBytes, 0, 4000));
			
			List<SysConfig> configs=baseService.queryConfigByKeyWithCache(CASSANDRA_SWITCH);
			for(SysConfig sys : configs){
				if(StringHelper.matchSiteRule(sys.getConfigContent(), "CASSANDRA_ON")){
					logCassandra.batchInsert(systemLog);
					systemLogDao.add(SystemLogDao.namespace, systemLog);
				}
				if(StringHelper.matchSiteRule(sys.getConfigContent(), "CASSANDRA_OFF")){
					systemLogDao.add(SystemLogDao.namespace, systemLog);
				}
				if(StringHelper.matchSiteRule(sys.getConfigContent(), "CASSANDRA_OL")){
					logCassandra.batchInsert(systemLog);
				}
			}
			return 1;
		} catch (Exception e) {
			log.error("插入操作日志失败，失败信息为：", e);
			return 0;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(String keyword1, String keyword2, String keyword3, Long keyword4, String content, Long type) {
		SystemLog systemLog = new SystemLog();
		systemLog.setKeyword1(keyword1);
		systemLog.setKeyword2(keyword2);
		systemLog.setKeyword3(keyword3);
		systemLog.setKeyword4(keyword4);
		systemLog.setContent(content);
		systemLog.setType(type);
		
		return add(systemLog);
	}
	
	public List<SystemLog> queryByParams(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return systemLogDao.queryByParams(params);
	}

	public Integer totalSizeByParams(Map<String, Object> params) {
		return systemLogDao.totalSizeByParams(params);
	}

	public Integer totalSize(String code) {
		return logCassandra.totalSize(code);
	}

	@Override
	public List<SystemLog> queryByCassandra(String code, Pager<SystemLog> pager) {
		// TODO Auto-generated method stub
		return logCassandra.getPage(code, pager);
	}
}
