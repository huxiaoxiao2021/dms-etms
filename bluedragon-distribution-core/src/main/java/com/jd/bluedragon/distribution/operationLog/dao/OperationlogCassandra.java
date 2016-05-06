package com.jd.bluedragon.distribution.operationLog.dao;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Function;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.cassandra.*;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.utils.JsonHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class OperationlogCassandra {

	private static final Logger logger = LoggerFactory.getLogger(OperationlogCassandra.class);

    @Value("${cassandra.consistencyLevel.default}")
    protected ConsistencyLevel  consistencyLevel;
    
    @Value("${cassandra.ttl}")
    protected long  ttl;
    
    public static final int SIZE = 2000;
	
    @Resource
    private BaseCassandraDao baseCassandraDao;
    private PreparedStatement preparedwaybill ;
    private PreparedStatement preparedpickCode;
    private PreparedStatement preparedpackage ;
    private PreparedStatement preparedbox ;
    
    private PreparedStatement preparedswaybill ;
    private PreparedStatement preparedspickCode;
    private PreparedStatement preparedspackage ;
    private PreparedStatement preparedsbox ;

    public OperationlogCassandra() {
    	
    }

	private PreparedStatement preparedwaybill() {
		RegularStatement toPrepare = new SimpleStatement(
				"insert into operationlogwaybill ( code , uu_id  , body,time) values ( ?,?,?,?) USING TTL " + ttl
						+ ";");
		toPrepare.setConsistencyLevel(consistencyLevel);
		return baseCassandraDao.getSession().prepare(toPrepare);
	}
    
	private PreparedStatement preparedpickCode() {
		RegularStatement toPrepare = new SimpleStatement(
				"insert into operationlogpick ( code , uu_id  , body,time) values ( ?,?,?,?) USING TTL " + ttl + ";");
		toPrepare.setConsistencyLevel(consistencyLevel);

		return baseCassandraDao.getSession().prepare(toPrepare);
	}
    
	private PreparedStatement preparedpackage() {
		RegularStatement toPrepare = new SimpleStatement(
				"insert into operationlogpackage ( code , uu_id  , body,time) values ( ?,?,?,?) USING TTL " + ttl
						+ ";");
		toPrepare.setConsistencyLevel(consistencyLevel);

		return baseCassandraDao.getSession().prepare(toPrepare);
	}
    
	private PreparedStatement preparedbox() {
		RegularStatement toPrepare = new SimpleStatement(
				"insert into operationlogbox ( code , uu_id  , body,time) values ( ?,?,?,?) USING TTL " + ttl + ";");
		toPrepare.setConsistencyLevel(consistencyLevel);

		return baseCassandraDao.getSession().prepare(toPrepare);
	}
   
	public void batchInsert(OperationLog log) {
		try {
			long startTime = System.currentTimeMillis();
			List<BoundStatement> bstatementList = new ArrayList<BoundStatement>();
			Map<String, Object> values = new HashMap<String, Object>();
			long operateTime = new Date().getTime();
			if(log.getOperateTime()!=null)
				operateTime = log.getOperateTime().getTime();

			if (log.getWaybillCode() != null && !log.getWaybillCode().isEmpty()) {
				if (preparedwaybill == null)
					preparedwaybill = preparedwaybill();
				BoundStatement bStatement = preparedwaybill.bind(log.getWaybillCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJson(log), operateTime);
				bstatementList.add(bStatement);
			}
			if (log.getBoxCode() != null && !log.getBoxCode().isEmpty()) {
				if (preparedbox == null)
					preparedbox = preparedbox();
				BoundStatement bStatement = preparedbox.bind(log.getBoxCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJson(log), operateTime);
				bstatementList.add(bStatement);
			}
			if (log.getPackageCode() != null && !log.getPackageCode().isEmpty()) {
				if (preparedpackage == null)
					preparedpackage = preparedpackage();
				BoundStatement bStatement = preparedpackage.bind(log.getPackageCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJson(log), operateTime);
				bstatementList.add(bStatement);
			}
			if (log.getPickupCode() != null && !log.getPickupCode().isEmpty()) {
				if (preparedpickCode == null)
					preparedpickCode = preparedpickCode();
				BoundStatement bStatement = preparedpickCode().bind(log.getPickupCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJson(log), operateTime);
				bstatementList.add(bStatement);
			}

			baseCassandraDao.batchInsert(bstatementList, values);
			logger.info("OperationlogCassandra batchInsert execute success cost:"
					+ (System.currentTimeMillis() - startTime) + "ms");
		} catch (Exception e) {
			logger.error("添加操作日志异常 异常原因：", e);
		}
	}
    
	private PreparedStatement preparedSelectBywaybill() {
		RegularStatement toPrepare = new SimpleStatement(
				"select * from operationlogwaybill  where code = ? order by time  ");
		toPrepare.setConsistencyLevel(consistencyLevel);
		return baseCassandraDao.getSession().prepare(toPrepare);
	}

	private PreparedStatement preparedSelectBypickcode() {
		RegularStatement toPrepare = new SimpleStatement(
				"select * from operationlogpick  where code = ? order by time  ");
		toPrepare.setConsistencyLevel(consistencyLevel);

		return baseCassandraDao.getSession().prepare(toPrepare);
	}

	private PreparedStatement preparedSelectBypackagecode() {
		RegularStatement toPrepare = new SimpleStatement(
				"select * from operationlogpackage  where code = ? order by time  ");
		toPrepare.setConsistencyLevel(consistencyLevel);

		return baseCassandraDao.getSession().prepare(toPrepare);
	}

	private PreparedStatement preparedSelectByboxcode() {
		RegularStatement toPrepare = new SimpleStatement(
				"select * from operationlogbox  where code = ? order by time  ");
		toPrepare.setConsistencyLevel(consistencyLevel);

		return baseCassandraDao.getSession().prepare(toPrepare);
	}

	public List<OperationLog> getPage(String code, String type ,Pager<OperationLog> pager) {
		List<OperationLog> list = new ArrayList<OperationLog>();
		long startTime = System.currentTimeMillis();
		try {
			BoundStatement bs = null;
			if (type.equals("waybill")) {
				if (preparedswaybill == null)
					preparedswaybill = preparedSelectBywaybill();
				else
					bs = preparedswaybill.bind(code);
			}
			if (type.equals("pick")) {
				if (preparedspickCode == null)
					preparedspickCode = preparedSelectBypickcode();
				else
					bs = preparedspickCode.bind(code);
			}
			if (type.equals("package")){
				if (preparedspackage == null)
					preparedspackage = preparedSelectBypackagecode();
				else
					bs = preparedspackage.bind(code);
			}
			if (type.equals("box")){
				if (preparedsbox == null)
					preparedsbox = preparedSelectByboxcode();
				else
					bs = preparedsbox.bind(code);
			}
			bs.setFetchSize(pager.getPageSize());
			PagingState pagingState = null;
			ResultSet rs = null;
			if(pager.getPageNo()==1)
				rs = baseCassandraDao.preparedSelectBycode(bs);
			else{
				rs = baseCassandraDao.preparedSelectBycode(bs);
				pagingState =rs.getExecutionInfo().getPagingState();
				for(int i=2 ;i<=pager.getPageNo();i++){
					bs.setPagingState(pagingState);
					rs = baseCassandraDao.preparedSelectBycode(bs);
					pagingState =rs.getExecutionInfo().getPagingState();
				}
			}
			list = rsToList(rs, new RowToOrder());
			logger.info("OperationlogCassandra getPage execute success cost:" + (System.currentTimeMillis() - startTime)
					+ "ms");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("查询操作日志异常 异常原因：", e);
		}

		return list;
	}
	
	public int totalSize(String code, String type) {
		long startTime = System.currentTimeMillis();
		int size =0;
		try {
			BoundStatement bs = null;
			if (type.equals("waybill")) {
				if (preparedswaybill == null)
					preparedswaybill = preparedSelectBywaybill();
				else
					bs = preparedswaybill.bind(code);
			}
			if (type.equals("pick")) {
				if (preparedspickCode == null)
					preparedspickCode = preparedSelectBypickcode();
				else
					bs = preparedspickCode.bind(code);
			}
			if (type.equals("package")){
				if (preparedspackage == null)
					preparedspackage = preparedSelectBypackagecode();
				else
					bs = preparedspackage.bind(code);
			}
			if (type.equals("box")){
				if (preparedsbox == null)
					preparedsbox = preparedSelectByboxcode();
				else
					bs = preparedsbox.bind(code);
			}
			ResultSet rs = baseCassandraDao.preparedSelectBycode(bs);
			size = rs.getAvailableWithoutFetching();
			logger.info("OperationlogCassandra totalSize execute success cost:" + (System.currentTimeMillis() - startTime)
					+ "ms");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("查询操作日志异常 异常原因：", e);
		}

		return size;
	}
	
	public static ArrayList<OperationLog> rsToList(ResultSet rs, Function<Row, OperationLog> rowToObject) {
		ArrayList<OperationLog> al = new ArrayList<OperationLog>();
		int available = rs.getAvailableWithoutFetching();
		OperationLog log = null;
		for (int i = 0; i < available; i++) {
			Row row = rs.one();
			log = rowToObject.apply(row);
			al.add(log);
		}
		return al;
	}
	
    private static final String BODY="body";
	
	public static final class RowToOrder implements Function<Row, OperationLog> {
		@Override
		public OperationLog apply(Row row) {
			OperationLog log = new OperationLog();
			log = JsonHelper.fromJson(row.getString(BODY), OperationLog.class);
			return log;
		}
	}
}
