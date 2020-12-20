package com.jd.bluedragon.distribution.web.sqlkit;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.sqlkit.domain.Sqlkit;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.Date;
import java.util.*;

@Controller
@RequestMapping("/sqlkit")
public class SqlkitController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

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

	@Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
	@RequestMapping("/toView")
	public String toView(Model model) {
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		log.info("访问sqlkit/toView用户erp账号：[{}]",erpUser.getUserCode());

		if (!SqlkitController.queryUsers.contains(erpUser.getUserCode().toLowerCase())) {
			log.info("用户erp账号：{}不在查询用户列表中,跳转到/index",erpUser.getUserCode());
			return "index";
		}

		setDataSourceNames(model);

		return "sqlkit/sqlkit";
	}


	@RequestMapping(value = "/checkJddl",method = RequestMethod.GET)
	@ResponseBody
	public List<String> checkJddl(Integer checkNum) {
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Connection connection = null;
		if(checkNum == null){
			checkNum = 8;
		}
		List<String> querySqls = new ArrayList<>();
		List<String> updateSqls = new ArrayList<>();
		List<String> results = new ArrayList<>();
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '761856' and send_d_id = '1339834213315338240'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '761856' and send_d_id = '1339834213315338240'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '706644' and send_d_id = '1339836724780679168'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '706644' and send_d_id = '1339836724780679168'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '680084' and send_d_id = '1339837260254814208'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '680084' and send_d_id = '1339837260254814208'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '654778' and send_d_id = '1339843347829866496'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '654778' and send_d_id = '1339843347829866496'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '682272' and send_d_id = '1339833533510844416'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '682272' and send_d_id = '1339833533510844416'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '865620' and send_d_id = '1339838654080765952'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '865620' and send_d_id = '1339838654080765952'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '1358228' and send_d_id = '1339846563946631168'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '1358228' and send_d_id = '1339846563946631168'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '1367532' and send_d_id = '1339508010704297984'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '1367532' and send_d_id = '1339508010704297984'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '729610' and send_d_id = '1339847272125554689'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '729610' and send_d_id = '1339847272125554689'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '1345128' and send_d_id = '1339847660912361472'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '1345128' and send_d_id = '1339847660912361472'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '1345176' and send_d_id = '1339830811315286016'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '1345176' and send_d_id = '1339830811315286016'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '847566' and send_d_id = '1339847716461760512'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '847566' and send_d_id = '1339847716461760512'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '847638' and send_d_id = '1339842736744972288'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '847638' and send_d_id = '1339842736744972288'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '681840' and send_d_id = '1339849032105840640'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '681840' and send_d_id = '1339849032105840640'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '700318' and send_d_id = '1339849695896391680'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '700318' and send_d_id = '1339849695896391680'");
		querySqls.add("select EXCUTE_COUNT from send_d where create_site_code = '877566' and send_d_id = '1339590626442493952'");
		updateSqls.add("update send_d set EXCUTE_COUNT = "+checkNum+" where create_site_code = '877566' and send_d_id = '1339590626442493952'");

		try{
			connection = ((DataSource)SpringHelper.getBean("jddlShardingDataSource")).getConnection();
			int i = 0;
			for(String querySql : querySqls){
				String result = "第"+(i+1)+"个库 ";

				try {
					int updateResult = connection.prepareStatement(updateSqls.get(i)).executeUpdate();
					result += updateResult == 1 ?"更新成功 ":"更新失败"+updateResult+"行 ";
				}catch (Exception e){
					log.error(updateSqls.get(i),e);
					result += "更新失败 ";
				}
				try {
					resultSet = connection.prepareStatement(querySql).executeQuery();
					ResultSetMetaData rsmd = resultSet.getMetaData();
					int columnCount = rsmd.getColumnCount();// 获得列数
					List<String> columnList = setColumnList(rsmd, columnCount);
					List<Map<String, Object>> rowList = setRows(resultSet, rsmd, columnCount);
					int rowCount = rowList.size();

					result += "查询成功，返回内容"+rowCount+"条"+columnList.get(0)+"值为"+rowList.get(0).get(columnList.get(0)) ;

				}catch (Exception e){
					log.error(querySql,e);
					result += "查询失败 ";
				}
				results.add(result);
				i++;
			}

		}catch (Exception e){
			log.error(e.getMessage(),e);
		}finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException se) {
				this.log.error("关闭文件流发生异常！", se);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException se) {
				this.log.error("关闭PreparedStatement发生异常！", se);
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
				this.log.error("关闭文件流发生异常！", se);
			}
		}


		return results;
	}


	private void setDataSourceNames(Model model) {
		List<String> dataSourceNames = Arrays.asList(PropertiesHelper.newInstance()
		        .getValue("sqlkitDataSourceName").split(Constants.SEPARATOR_COMMA));
		model.addAttribute("dataSourceNames", dataSourceNames);
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
	@RequestMapping(value = "/executeSql", method = { RequestMethod.GET, RequestMethod.POST })
	public String executeSql(Sqlkit sqlkit, @SuppressWarnings("rawtypes") Pager pager, Model model) {
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Connection connection = null;
		int errorCount = 0;

		try {
			setDataSourceNames(model);
			setDataSource(sqlkit.getDataSourceName());

			String sql = sqlkit.getSqlContent().trim().replaceAll("\r", "").replaceAll("\n", "");

			connection = this.dataSource.getConnection();
			
			if (sql.toLowerCase().startsWith("select")) {
				pager = setPager(pager);
				setTotalSize(pager, connection, sql);
				String sqlExecute = sql + " limit ?,?" ;
                pstmt = connection.prepareStatement(sqlExecute);
                pstmt.setQueryTimeout(StringHelper.isEmpty(SqlkitController.STATEMENT_TIME_OUT)?30:Integer.valueOf(SqlkitController.STATEMENT_TIME_OUT));
                pstmt.setInt(1, pager.getStartIndex());
                pstmt.setInt(2, pager.getPageSize());
				resultSet = pstmt.executeQuery();
				log.info("访问sqlkit/toView用户erp账号:[{}]执行sql[{}]",erpUser.getUserCode(), sql);
				ResultSetMetaData rsmd = resultSet.getMetaData();
				int columnCount = rsmd.getColumnCount();// 获得列数
				List<String> columnList = setColumnList(rsmd, columnCount);
				List<Map<String, Object>> rowList = setRows(resultSet, rsmd, columnCount);
				int rowCount = rowList.size();
				log.debug("结果条数={}", rowCount);
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
					log.info("访问sqlkit/toView用户erp账号:[{}]执行sql[{}]",erpUser.getUserCode(), sql);
				} else {
					model.addAttribute("message", "你没有权限执行update/insert");
				}
				model.addAttribute("displayPageBar", false);
			} else {
				model.addAttribute("message", "只支持select/update/insert");
				model.addAttribute("displayPageBar", false);
			}
			model.addAttribute("sqlkitDto", sqlkit);
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			e.printStackTrace(ps);
			String msg = baos.toString();
			model.addAttribute("error", msg);
			errorCount++;
			log.error("executeSql 异常：",e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException se) {
				this.log.error("关闭文件流发生异常！", se);
			}
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException se) {
                this.log.error("关闭PreparedStatement发生异常！", se);
            }
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
				this.log.error("关闭文件流发生异常！", se);
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
            StringBuilder sqlBuilder = new StringBuilder("select count(1) from (");
            sqlBuilder.append(sql);
            sqlBuilder.append(") AS b");
            pstmt = connection.prepareStatement(sqlBuilder.toString());
			resultSet = pstmt.executeQuery();
			resultSet.next();
			pager.setTotalSize(resultSet.getInt(1));
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException se) {
				this.log.error("关闭文件流发生异常！", se);
			}
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException se) {
                this.log.error("关闭PreparedStatement发生异常！", se);
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
