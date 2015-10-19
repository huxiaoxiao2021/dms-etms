package com.jd.bluedragon.distribution.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Function;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.utils.JsonHelper;
import java.util.ArrayList;
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

			if (log.getWaybillCode() != null && !log.getWaybillCode().isEmpty()) {
				if (preparedwaybill == null)
					preparedwaybill = preparedwaybill();
				BoundStatement bStatement = preparedwaybill.bind(log.getWaybillCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJson(log), log.getOperateTime().getTime());
				bstatementList.add(bStatement);
			}
			if (log.getBoxCode() != null && !log.getBoxCode().isEmpty()) {
				if (preparedbox == null)
					preparedbox = preparedbox();
				BoundStatement bStatement = preparedbox.bind(log.getBoxCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJson(log), log.getOperateTime().getTime());
				bstatementList.add(bStatement);
			}
			if (log.getPackageCode() != null && !log.getPackageCode().isEmpty()) {
				if (preparedpackage == null)
					preparedpackage = preparedpackage();
				BoundStatement bStatement = preparedpackage.bind(log.getPackageCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJson(log), log.getOperateTime().getTime());
				bstatementList.add(bStatement);
			}
			if (log.getPickupCode() != null && !log.getPickupCode().isEmpty()) {
				if (preparedpickCode == null)
					preparedpickCode = preparedpickCode();
				BoundStatement bStatement = preparedpickCode().bind(log.getPickupCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJson(log), log.getOperateTime().getTime());
				bstatementList.add(bStatement);
			}

			baseCassandraDao.batchInsert(bstatementList, values);
			logger.info("OperationlogCassandra batchInsert execute success cost:"
					+ (System.currentTimeMillis() - startTime) + "ms");
		} catch (Exception e) {
			logger.error("添加操作日志异常 异常原因：", e);
		}
	}
    
	private PreparedStatement preparedSelectBywaybill(String code) {
		RegularStatement toPrepare = new SimpleStatement(
				"select * from operationlogwaybill  where code = ? order by time  ");
		toPrepare.setConsistencyLevel(consistencyLevel);
		return baseCassandraDao.getSession().prepare(toPrepare);
	}

	private PreparedStatement preparedSelectBypickcode(String code) {
		RegularStatement toPrepare = new SimpleStatement(
				"select * from operationlogpick  where code = ? order by time  ");
		toPrepare.setConsistencyLevel(consistencyLevel);

		return baseCassandraDao.getSession().prepare(toPrepare);
	}

	private PreparedStatement preparedSelectBypackagecode(String code) {
		RegularStatement toPrepare = new SimpleStatement(
				"select * from operationlogpackage  where code = ? order by time  ");
		toPrepare.setConsistencyLevel(consistencyLevel);

		return baseCassandraDao.getSession().prepare(toPrepare);
	}

	private PreparedStatement preparedSelectByboxcode(String code) {
		RegularStatement toPrepare = new SimpleStatement(
				"select * from operationlogbox  where code = ? order by time  ");
		toPrepare.setConsistencyLevel(consistencyLevel);

		return baseCassandraDao.getSession().prepare(toPrepare);
	}

	public List<OperationLog> getPage(String code, String type) {
		List<OperationLog> list = new ArrayList<OperationLog>();
		long startTime = System.currentTimeMillis();
		try {
			BoundStatement bs = null;
			if (type.equals("waybill"))
				bs = preparedSelectBywaybill(code).bind(code);
			if (type.equals("pick"))
				bs = preparedSelectBypickcode(code).bind(code);
			if (type.equals("package"))
				bs = preparedSelectBypackagecode(code).bind(code);
			if (type.equals("box"))
				bs = preparedSelectByboxcode(code).bind(code);
			bs.setFetchSize(SIZE);
			ResultSet rs = baseCassandraDao.preparedSelectBycode(bs);
			list = rsToList(rs, new RowToOrder());
			logger.info("OperationlogCassandra getPage execute success cost:" + (System.currentTimeMillis() - startTime)
					+ "ms");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("查询操作日志异常 异常原因：", e);
		}

		return list;
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
