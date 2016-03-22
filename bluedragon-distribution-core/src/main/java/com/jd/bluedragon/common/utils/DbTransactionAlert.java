package com.jd.bluedragon.common.utils;


import com.jd.bluedragon.core.dbs.transaction.TransactionManagerAlert;

public class DbTransactionAlert implements TransactionManagerAlert {

    @Override
    public void sendMessage(String dataSource, String appServer, AlertType alertType) {
        System.out.println("datasource=" + dataSource + ",appServer=" + appServer);
    }

	@Override
	public void sendRequests(String dataSource, String requests,
			AlertType alertType) {
		System.out.println("datasource=" + dataSource + ",requests=" + requests +",alertType=" + alertType);
	}

}
