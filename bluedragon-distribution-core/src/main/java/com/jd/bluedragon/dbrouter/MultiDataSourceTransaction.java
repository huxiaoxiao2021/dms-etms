package com.jd.bluedragon.dbrouter;

import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * <P>多数据源切换，支持事务</P>
 *
 * @author weixiaofeng12
 */
public class MultiDataSourceTransaction implements Transaction {
  private static final Logger logger = LoggerFactory.getLogger(MultiDataSourceTransaction.class);

  private final DataSource dataSource;

  private Connection mainConnection;

  private String mainDatabaseIdentification;

  private ConcurrentMap<String, Connection> otherConnectionMap;


  private boolean isConnectionTransactional;

  private boolean autoCommit;


  public MultiDataSourceTransaction(DataSource dataSource) {
    notNull(dataSource, "No DataSource specified");
    this.dataSource = dataSource;
    otherConnectionMap = new ConcurrentHashMap<>();
    mainDatabaseIdentification= DynamicDataSourceHolders.getDataSource().name();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Connection getConnection() throws SQLException {
    String databaseIdentification = DynamicDataSourceHolders.getDataSource().name();
    if (null==databaseIdentification || "".equals(databaseIdentification)){
      databaseIdentification = DynamicDataSourceType.DEFAULT.name();
    }
    if (databaseIdentification.equals(mainDatabaseIdentification)) {
      if (mainConnection != null) return mainConnection;
      else {
        openMainConnection();
        mainDatabaseIdentification =databaseIdentification;
        return mainConnection;
      }
    } else {
      if (!otherConnectionMap.containsKey(databaseIdentification)) {
        try {
          Connection conn = dataSource.getConnection();
          otherConnectionMap.put(databaseIdentification, conn);
        } catch (SQLException ex) {
          throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", ex);
        }
      }
      return otherConnectionMap.get(databaseIdentification);
    }

  }


  private void openMainConnection() throws SQLException {
    this.mainConnection = DataSourceUtils.getConnection(this.dataSource);
    this.autoCommit = this.mainConnection.getAutoCommit();
    this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.mainConnection, this.dataSource);

    if (logger.isDebugEnabled()) {
      logger.debug(
          "JDBC Connection ["
              + this.mainConnection
              + "] will"
              + (this.isConnectionTransactional ? " " : " not ")
              + "be managed by Spring");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void commit() throws SQLException {
    if (this.mainConnection != null && !this.isConnectionTransactional && !this.autoCommit) {
      if (logger.isDebugEnabled()) {
        logger.debug("Committing JDBC Connection [" + this.mainConnection + "]");
      }
      this.mainConnection.commit();
      for (Connection connection : otherConnectionMap.values()) {
        connection.commit();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void rollback() throws SQLException {
    if (this.mainConnection != null && !this.isConnectionTransactional && !this.autoCommit) {
      if (logger.isDebugEnabled()) {
        logger.debug("Rolling back JDBC Connection [" + this.mainConnection + "]");
      }
      this.mainConnection.rollback();
      for (Connection connection : otherConnectionMap.values()) {
        connection.rollback();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws SQLException {
    DataSourceUtils.releaseConnection(this.mainConnection, this.dataSource);
    for (Connection connection : otherConnectionMap.values()) {
      DataSourceUtils.releaseConnection(connection, this.dataSource);
    }
  }

}
