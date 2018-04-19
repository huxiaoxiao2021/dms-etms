package com.jd.bluedragon.common.dao.migration;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import com.jd.ql.framework.migration.replication.transaction.ReplicationSqlSessionFactory;
import javax.annotation.Resource;

/**
 * 支持动态配置的双写复制sqlSession工厂，动态开关支持从dcam中控制。
 * @author yangwubing
 *
 */
public class DynamicConfigReplicationSqlSessionFactory extends ReplicationSqlSessionFactory {

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

	public DynamicConfigReplicationSqlSessionFactory( SqlSessionFactory targetSqlSessionFactory) {
		 super(targetSqlSessionFactory);
	}

	@Override
	public boolean isEnable() {
		Boolean value = uccPropertyConfiguration.getMigrationDbBackupReplicateEnable();
		if(value!=null){
			return value;
		}
		return super.isEnable();
	}

	@Override
	public boolean isIgnoreExceptionWhenReplication() {
        Boolean value = uccPropertyConfiguration.getMigrationDbBackupReplicateIgnoreExp();
		if(value!=null){
			return value;
		}
		return super.isIgnoreExceptionWhenReplication();
	}
	
}
