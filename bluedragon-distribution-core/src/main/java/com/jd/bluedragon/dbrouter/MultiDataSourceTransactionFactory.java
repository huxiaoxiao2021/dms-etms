package com.jd.bluedragon.dbrouter;



import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;

/**
 * <P>支持Service内多数据源切换的Factory</P>
 *
 */
public class MultiDataSourceTransactionFactory extends SpringManagedTransactionFactory {
  @Override
  public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
    return new MultiDataSourceTransaction(dataSource);
  }
}
