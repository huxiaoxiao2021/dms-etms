package com.jd.bluedragon.common.dao.migration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.ql.dcam.config.ConfigManager;
import com.jd.ql.framework.migration.replication.transaction.ReplicationSqlSessionFactory;

/**
 * 支持动态配置的双写复制sqlSession工厂，动态开关支持从dcam中控制。
 * @author yangwubing
 *
 */
public class DynamicConfigReplicationSqlSessionFactory extends
		ReplicationSqlSessionFactory {
	
	public static final String WRITE_REPLICATION_ENBALED_KEY = "migration.db.backup.replicate.enable";
	
	public static final String WRITE_IGNORE_EXCEPTION_WHEN_REPLICATION_KEY = "migration.db.backup.replicate.ignoreExp";
	
	
	@Autowired
	private ConfigManager configManager;

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public DynamicConfigReplicationSqlSessionFactory(
			SqlSessionFactory targetSqlSessionFactory) {
		 super(targetSqlSessionFactory);
	}

	@Override
	public boolean isEnable() {
		String value = configManager.getProperty(WRITE_REPLICATION_ENBALED_KEY);
		if(value!=null){
			return "true".equalsIgnoreCase(value);
		}
		return super.isEnable();
	}

	@Override
	public boolean isIgnoreExceptionWhenReplication() {
		String value = configManager.getProperty(WRITE_IGNORE_EXCEPTION_WHEN_REPLICATION_KEY);
		if(value!=null){
			return "true".equalsIgnoreCase(value);
		}
		return super.isIgnoreExceptionWhenReplication();
	}
	
}
