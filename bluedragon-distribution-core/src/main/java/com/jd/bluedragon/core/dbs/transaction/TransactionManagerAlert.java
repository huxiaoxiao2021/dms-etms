package com.jd.bluedragon.core.dbs.transaction;

public interface TransactionManagerAlert {
	public enum AlertType {
		commit, rollback, afterCommit
	}

	void sendMessage(String dataSource, String appServer, AlertType alertType);

	void sendRequests(String dataSource, String requests, AlertType alertType);
}
