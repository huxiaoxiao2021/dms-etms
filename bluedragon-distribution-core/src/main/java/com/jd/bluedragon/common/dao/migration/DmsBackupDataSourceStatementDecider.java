package com.jd.bluedragon.common.dao.migration;

import com.jd.ql.framework.migration.replication.transaction.Statement;
import com.jd.ql.framework.migration.replication.transaction.StatementDecider;

public class DmsBackupDataSourceStatementDecider implements StatementDecider {

	@Override
	public boolean decide(Statement statement) {
		return true;
	}

}
