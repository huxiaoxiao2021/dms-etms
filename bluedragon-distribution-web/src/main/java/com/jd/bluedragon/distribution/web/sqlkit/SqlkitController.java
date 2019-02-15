package com.jd.bluedragon.distribution.web.sqlkit;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.sqlkit.domain.Sqlkit;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.bluedragon.utils.StringHelper;

@Controller
@RequestMapping("/sqlkit")
public class SqlkitController {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final String MODIFY_USERS = "modifyUsers";
	private static final String QUERY_USERS = "queryUsers";

	private static final List<String> queryUsers;
	private static final List<String> modifyUsers;
    private static final String STATEMENT_TIME_OUT;
	private DataSource dataSource = null;

	static {
		queryUsers = Arrays.asList(PropertiesHelper.newInstance()
		        .getValue(SqlkitController.QUERY_USERS).split(Constants.SEPARATOR_COMMA));
		modifyUsers = Arrays.asList(PropertiesHelper.newInstance()
		        .getValue(SqlkitController.MODIFY_USERS).split(Constants.SEPARATOR_COMMA));
		STATEMENT_TIME_OUT=PropertiesHelper.newInstance().getValue("statementTimeOut");
	}

	public void setDataSource(String dataSourceName) {
		this.dataSource = (DataSource) SpringHelper.getBean(dataSourceName);
	}

	@RequestMapping("/toView")
	public String toView(Model model) {
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		logger.info("访问sqlkit/toView用户erp账号：[" + erpUser.getUserCode() + "]");

		if (!SqlkitController.queryUsers.contains(erpUser.getUserCode().toLowerCase())) {
			logger.info("用户erp账号：" + erpUser.getUserCode() + "不在查询用户列表中,跳转到/index");
			return "index";
		}

		setDataSourceNames(model);

		return "sqlkit/sqlkit";
	}

	private void setDataSourceNames(Model model) {
		List<String> dataSourceNames = Arrays.asList(PropertiesHelper.newInstance()
		        .getValue("sqlkitDataSourceName").split(Constants.SEPARATOR_COMMA));
		model.addAttribute("dataSourceNames", dataSourceNames);
	}

	@RequestMapping(value = "/executeSql", method = { RequestMethod.GET, RequestMethod.POST })
	public String executeSql(Sqlkit sqlkit, @SuppressWarnings("rawtypes") Pager pager, Model model) {
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		Statement statement = null;
        PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Connection connection = null;
		int errorCount = 0;

		try {
			setDataSourceNames(model);
			setDataSource(sqlkit.getDataSourceName());

			String sql = sqlkit.getSqlContent().trim().replaceAll("\r", "").replaceAll("\n", "");

			connection = this.dataSource.getConnection();
			
			statement = connection.createStatement();
			
			statement.setQueryTimeout(StringHelper.isEmpty(SqlkitController.STATEMENT_TIME_OUT)?30:Integer.valueOf(SqlkitController.STATEMENT_TIME_OUT));

			if (sql.toLowerCase().startsWith("select")) {
				pager = setPager(pager);
				setTotalSize(pager, connection, sql);
				String sqlExecute = sql + " limit " + pager.getStartIndex() + "," + pager.getPageSize();
				resultSet = statement.executeQuery(sqlExecute);
				logger.info("访问sqlkit/toView用户erp账号:[" + erpUser.getUserCode() + "]执行sql[" + sql
				        + "]");
				ResultSetMetaData rsmd = resultSet.getMetaData();
				int columnCount = rsmd.getColumnCount();// 获得列数
				List<String> columnList = setColumnList(rsmd, columnCount);
				List<Map<String, Object>> rowList = setRows(resultSet, rsmd, columnCount);
				int rowCount = resultSet.getRow();
				logger.debug("结果条数=" + rowCount);
				model.addAttribute("rowList", rowList);
				model.addAttribute("columnList", columnList);
				model.addAttribute("columnsize", columnList.size());
				model.addAttribute("pager", pager);
				if (rowList.size() > 0) {
					model.addAttribute("displayPageBar", true);
				} else {
					model.addAttribute("displayPageBar", false);
				}
			} else if (sql.toLowerCase().startsWith("update")
			        || sql.toLowerCase().startsWith("insert")) {
				if (SqlkitController.modifyUsers.contains(erpUser.getUserCode().toLowerCase())) {
                    pstmt = connection.prepareStatement(sql);
					int changeRows = pstmt.executeUpdate();
//					connection.commit();
					model.addAttribute("message", "影响行数" + changeRows);
					logger.info("访问sqlkit/toView用户erp账号:[" + erpUser.getUserCode() + "]执行sql["
					        + sql + "]");
				} else {
					model.addAttribute("message", "你没有权限执行update/insert");
				}
				model.addAttribute("displayPageBar", false);
			} else {
				model.addAttribute("message", "只支持select/update/insert");
				model.addAttribute("displayPageBar", false);
			}
			model.addAttribute("sqlkitDto", sqlkit);
		} catch (SQLException e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			e.printStackTrace(ps);
			String msg = baos.toString();
			model.addAttribute("error", msg);
			errorCount++;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException se) {
				this.logger.error("关闭文件流发生异常！", se);
			}

			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException se) {
				this.logger.error("关闭文件流发生异常！", se);
			}
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException se) {
                this.logger.error("关闭PreparedStatement发生异常！", se);
            }
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
				this.logger.error("关闭文件流发生异常！", se);
			}
		}

		if (errorCount == 0) {
			return "sqlkit/sqlkit";
		} else {
			return "sqlkit/sqlkitError";
		}

	}

	@SuppressWarnings("rawtypes")
	private void setTotalSize(Pager pager, Connection connection, String sql) throws SQLException {
		ResultSet resultSet = null;
        PreparedStatement pstmt = null;
		try {
            String sqlCount = "select count(1) from (?) AS b";
            pstmt = connection.prepareStatement(sqlCount);
            pstmt.setString(1, sql);
			resultSet = pstmt.executeQuery();
			resultSet.next();
			pager.setTotalSize(resultSet.getInt(1));
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException se) {
				this.logger.error("关闭文件流发生异常！", se);
			}
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException se) {
                this.logger.error("关闭PreparedStatement发生异常！", se);
            }
		}
	}

	@SuppressWarnings("rawtypes")
	private Pager setPager(Pager pager) {
		// 设置分页对象
		if (pager == null) {
			pager = new Pager(Pager.DEFAULT_PAGE_NO);
		} else {
			pager = new Pager(pager.getPageNo(), pager.getPageSize());
		}
		return pager;
	}

	private List<Map<String, Object>> setRows(ResultSet rs, ResultSetMetaData rsmd,
	        int columnCount) throws SQLException {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> rowMap = new HashMap<String, Object>();
			for (int k = 1; k <= columnCount; k++) {
				String columnName = rsmd.getColumnName(k);
				String columnType = rsmd.getColumnTypeName(k);
				Object columnValue = rs.getObject(columnName);
				if (!"L".equals(columnName)) {
					if (columnValue instanceof Date) {
						rowMap.put(columnName, DateHelper.formatDateTime((Date) columnValue));
					} else {
						rowMap.put(columnName, columnValue);
					}
				}
				logger.debug("列名:" + columnName + " 类型:" + columnType + " 列值:" + columnValue);
			}
			rows.add(rowMap);
		}
		return rows;
	}

	private List<String> setColumnList(ResultSetMetaData rsmd, int columnCount) throws SQLException {
		List<String> columnList = new ArrayList<String>();
		for (int k = 1; k <= columnCount; k++) {
			String columnName = rsmd.getColumnName(k);
			if (!"L".equals(columnName)) {
				columnList.add(columnName);
			}

		}
		return columnList;
	}
}
