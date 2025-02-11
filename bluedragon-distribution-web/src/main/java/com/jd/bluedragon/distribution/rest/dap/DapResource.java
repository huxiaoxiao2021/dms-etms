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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DapResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final String UN_DIV_TABLE_NAMES = "undivTableNames";
	private static final String TASK_TABLE_NAMES = "taskTableNames";
	private static final String DIV_TABLE_NAMES = "divTableNames";

	private static final List<String> unDivTableNames = new ArrayList<>();
	private static final List<String> taskTableNames = new ArrayList<>();
	private static final List<String> divTableNames = new ArrayList<>();

	private static final String STATEMENT_TIME_OUT;
	private DataSource dataSource = null;

	static {
		if(PropertiesHelper.newInstance().getValue(DapResource.UN_DIV_TABLE_NAMES)!=null){
			unDivTableNames.addAll(Arrays.asList(PropertiesHelper.newInstance().getValue(DapResource.UN_DIV_TABLE_NAMES).split(Constants.SEPARATOR_COMMA)));
		}
		if(PropertiesHelper.newInstance().getValue(DapResource.TASK_TABLE_NAMES)!=null){
			taskTableNames.addAll(Arrays.asList(PropertiesHelper.newInstance().getValue(DapResource.TASK_TABLE_NAMES).split(Constants.SEPARATOR_COMMA)));
		}
		if(PropertiesHelper.newInstance().getValue(DapResource.DIV_TABLE_NAMES)!=null){
			divTableNames.addAll(Arrays.asList(PropertiesHelper.newInstance().getValue(DapResource.DIV_TABLE_NAMES).split(Constants.SEPARATOR_COMMA)));
		}

		STATEMENT_TIME_OUT=PropertiesHelper.newInstance().getValue("statementTimeOut");
	}


	@GET
	@Path("/dap/info/undiv")
	public JdResponse<List<DapInfo>> getUndiv() {
		JdResponse<List<DapInfo>> response = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
		List<DapInfo> dapInfoList = getDapInfos(response, unDivTableNames, "dms_main_undiv");
		response.setData(dapInfoList);
		return response;
	}

	@GET
	@Path("/dap/info/task")
	public JdResponse<List<DapInfo>> getTask() {
		JdResponse<List<DapInfo>> response = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
		List<DapInfo> dapInfoList = getDapInfos(response, taskTableNames, "dms_main_task");
		response.setData(dapInfoList);
		return response;
	}

	@GET
	@Path("/dap/info/div")
	public JdResponse<List<DapInfo>> getDiv() {
		JdResponse<List<DapInfo>> response = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
		List<DapInfo> dapInfoList = getDapInfos(response, divTableNames, "jddlShardingDataSource");
		response.setData(dapInfoList);
		return response;
	}

    @GET
    @Path("/table/row/undiv")
    public JdResponse<List<DapInfo>> getUndivTableRows() {
        JdResponse<List<DapInfo>> response = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        List<DapInfo> dapInfoList = getTableRowInfo(response, "dms_main_undiv");
        response.setData(dapInfoList);
        return response;
    }

    @GET
    @Path("/table/row/task")
    public JdResponse<List<DapInfo>> getTaskTableRows() {
        JdResponse<List<DapInfo>> response = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        List<DapInfo> dapInfoList = getTableRowInfo(response, "dms_main_task");
        response.setData(dapInfoList);
        return response;
    }

	@GET
	@Path("/pdd/reprint/info/{dateGap}")
	public JdResponse<List<DapInfo>> getPddReprintInfo(@PathParam("dateGap") Integer dateGap) {
		JdResponse<List<DapInfo>> response = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

		List<DapInfo> dapInfoList = getPddInfo(response, DateToStringBeginOrEnd(dateGap, true), DateToStringBeginOrEnd(dateGap, false));
		response.setData(dapInfoList);
		return response;
	}

	private List<DapInfo> getDapInfos(JdResponse<List<DapInfo>> response, List<String> unDivTableNames, String dbBeanName) {
		Connection connection = null;

		List<DapInfo> dapInfoList = new ArrayList<>();
		try {
			this.dataSource = (DataSource) SpringHelper.getBean(dbBeanName);
			connection = this.dataSource.getConnection();
			for (String tableName : unDivTableNames) {
				DapInfo dapInfo = new DapInfo();
				Date createTime = getDateFromDb(tableName, "create_time", connection);
				Date updateTime = getDateFromDb(tableName, "update_time", connection);
				Date ts = getDateFromDb(tableName, "ts", connection);
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
			logger.debug(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
				this.logger.debug("关闭文件流发生异常！", se);
			}
		}
		return dapInfoList;
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
		} catch (Exception e) {
			logger.warn(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException se) {
				this.logger.warn("关闭文件流发生异常！", se);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException se) {
				this.logger.warn("关闭PreparedStatement发生异常！", se);
			}
		}
		return date;
	}

    private List<DapInfo> getTableRowInfo(JdResponse<List<DapInfo>> response, String dbBeanName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        List<DapInfo> dapInfoList = new ArrayList<>();

        try {
            this.dataSource = (DataSource) SpringHelper.getBean(dbBeanName);
            connection = this.dataSource.getConnection();
            String sql = "select table_name,table_rows from information_schema.tables  where table_schema='bd_dms_core' order by table_rows desc";
            pstmt = connection.prepareStatement(sql);
            pstmt.setQueryTimeout(StringHelper.isEmpty(DapResource.STATEMENT_TIME_OUT)?30:Integer.valueOf(DapResource.STATEMENT_TIME_OUT));
            resultSet = pstmt.executeQuery();
            DapInfo dapInfo = null;
            while (resultSet != null && resultSet.next()) {
                dapInfo = new DapInfo();
                dapInfo.setTableName(resultSet.getObject("table_name").toString());
                dapInfo.setRowCount(resultSet.getObject("table_rows").toString());

                dapInfoList.add(dapInfo);
            }
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_ERROR);
            response.setMessage(JdResponse.MESSAGE_ERROR);
            logger.debug(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException se) {
                this.logger.debug("关闭文件流发生异常！", se);
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException se) {
                this.logger.warn("关闭文件流发生异常！", se);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException se) {
                this.logger.warn("关闭PreparedStatement发生异常！", se);
            }
        }
        return dapInfoList;
    }


	private List<DapInfo> getPddInfo(JdResponse<List<DapInfo>> response, String startTime, String endTime) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		List<DapInfo> dapInfoList = new ArrayList<>();

		if (startTime == null || endTime == null) {
			return dapInfoList;
		}
		try {
			this.dataSource = (DataSource) SpringHelper.getBean("dms_main_undiv");
			connection = this.dataSource.getConnection();
			String sql = "select * from reprint_record where operate_time > '" + startTime + "' and operate_time < '" + endTime  + "' and bar_code like 'JDAP%' order by site_code";
			pstmt = connection.prepareStatement(sql);
			pstmt.setQueryTimeout(StringHelper.isEmpty(DapResource.STATEMENT_TIME_OUT)?30:Integer.valueOf(DapResource.STATEMENT_TIME_OUT));
			resultSet = pstmt.executeQuery();
			DapInfo dapInfo = null;
			while (resultSet != null && resultSet.next()) {
				dapInfo = new DapInfo();
				dapInfo.setSpace1(resultSet.getObject("bar_code").toString());
				dapInfo.setSpace2(resultSet.getObject("site_name") == null ? "" : resultSet.getObject("site_name").toString());
				dapInfo.setSpace3(resultSet.getObject("operator_name") == null ? "" : resultSet.getObject("operator_name").toString());
				dapInfo.setSpace4(resultSet.getObject("operate_time").toString());
				dapInfoList.add(dapInfo);
			}
		} catch (Exception e) {
			response.setCode(JdResponse.CODE_ERROR);
			response.setMessage(JdResponse.MESSAGE_ERROR);
			logger.debug(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
				this.logger.debug("关闭文件流发生异常！", se);
			}
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException se) {
				this.logger.warn("关闭文件流发生异常！", se);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException se) {
				this.logger.warn("关闭PreparedStatement发生异常！", se);
			}
		}
		return dapInfoList;
	}

	public String DateToStringBeginOrEnd(Integer dateGap, Boolean flag) {
		String time = null;
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar calendar1 = Calendar.getInstance();
		Date now = new Date();
		long dateLong = now.getTime() - 1000*3600*24*dateGap;

		Date date = new Date(dateLong);
		//获取某一天的0点0分0秒 或者 23点59分59秒
		if (flag) {
			calendar1.setTime(date);
			calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
					0, 0, 0);
			Date beginOfDate = calendar1.getTime();
			time = dateformat1.format(beginOfDate);
		}else{
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(date);
			calendar1.set(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH),
					23, 59, 59);
			Date endOfDate = calendar1.getTime();
			time = dateformat1.format(endOfDate);
		}

		return time;
	}
}
