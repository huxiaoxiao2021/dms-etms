package com.jd.bluedragon.distribution.rest.dap;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.sqlkit.domain.Sqlkit;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.Date;
import java.util.*;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DapResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final String TABLE_NAMES = "tableNames";
	private static final String ALLOW_USERS = "queryUsers";

	private static final List<String> queryUsers;
	private static final List<String> tableNames;
    private static final String STATEMENT_TIME_OUT;
	private DataSource dataSource = null;

	static {
		queryUsers = Arrays.asList(PropertiesHelper.newInstance().getValue(DapResource.ALLOW_USERS).split(Constants.SEPARATOR_COMMA));
		tableNames = Arrays.asList(PropertiesHelper.newInstance().getValue(DapResource.TABLE_NAMES).split(Constants.SEPARATOR_COMMA));
		STATEMENT_TIME_OUT=PropertiesHelper.newInstance().getValue("statementTimeOut");
	}


	@GET
	@Path("/dap/info/undiv")
	public JdResponse<List<DapInfo>> getUndiv() {
		JdResponse<List<DapInfo>> response = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		if (erpUser == null) {
			response.setCode(JdResponse.CODE_ERROR);
			response.setMessage("登录信息有误");
			return response;
		}
		if (! DapResource.queryUsers.contains(erpUser.getUserCode().toLowerCase())) {
			logger.info("用户erp账号：" + erpUser.getUserCode() + "不在查询用户列表中！");
			response.setCode(JdResponse.CODE_FAIL);
			response.setMessage("用户erp账号：" + erpUser.getUserCode() + "不在查询用户列表中！");
			return response;
		}

		Connection connection = null;

		List<DapInfo> dapInfoList = new ArrayList<>();
		try {
			this.dataSource = (DataSource) SpringHelper.getBean("dms_main_undiv");
			connection = this.dataSource.getConnection();
			for (String tableName : tableNames) {
				DapInfo dapInfo = new DapInfo();
				Date createTime = getDateFromDb(tableName,"create_time", connection);
				Date updateTime = getDateFromDb(tableName,"update_time", connection);
				Date ts = getDateFromDb(tableName,"ts", connection);
				dapInfo.setTableName(tableName);
				dapInfo.setCreateTime(createTime);
				dapInfo.setUpdateTime(updateTime);
				dapInfo.setTs(ts);
				Date now = new Date();
				dapInfo.setCreateTimeGap(differentDaysByMillisecond(createTime, now));
				dapInfo.setUpdateDateGap(differentDaysByMillisecond(updateTime, now));
				dapInfo.setTsGap(differentDaysByMillisecond(ts, now));

				dapInfoList.add(dapInfo);

			}
		} catch (Exception e) {
			response.setCode(JdResponse.CODE_ERROR);
			response.setMessage(JdResponse.MESSAGE_ERROR);
			logger.error(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
				this.logger.error("关闭文件流发生异常！", se);
			}
		}
		response.setData(dapInfoList);
		return response;
	}

	private Integer differentDaysByMillisecond(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return null;
		}
		return (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
	}

	private Date getDateFromDb(String tableName, String columnName, Connection connection) {
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Date date = null;
		try {
			String sql = "select " + columnName  + " from " + tableName + " limit 1";
			pstmt = connection.prepareStatement(sql);
			pstmt.setQueryTimeout(StringHelper.isEmpty(DapResource.STATEMENT_TIME_OUT)?30:Integer.valueOf(DapResource.STATEMENT_TIME_OUT));
			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				date = (Date) resultSet.getObject(columnName);
			}
			return date;
		} catch (Exception e) {
			logger.error(e);
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
		return date;
	}
}
