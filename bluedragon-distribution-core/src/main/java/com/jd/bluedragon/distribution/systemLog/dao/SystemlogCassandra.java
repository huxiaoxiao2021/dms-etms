package com.jd.bluedragon.distribution.systemLog.dao;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Function;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.cassandra.BaseCassandraDao;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.*;

public class SystemlogCassandra {

	private static final Logger log = LoggerFactory.getLogger(SystemlogCassandra.class);

    @Value("${cassandra.consistencyLevel.default}")
    protected ConsistencyLevel  consistencyLevel;
    
    @Value("${cassandra.ttl}")
    protected long  ttl;
    
    public static final int SIZE = 2000;
	
    @Resource
    private BaseCassandraDao baseCassandraDao;
    private PreparedStatement preparedwaybill ;
    
    private PreparedStatement preparedswaybill ;

    public SystemlogCassandra() {
    	
    }

	private PreparedStatement preparedwaybill() {
		RegularStatement toPrepare = new SimpleStatement(
				"insert into systemlogwaybill ( code , uu_id  , body,time) values ( ?,?,?,?) USING TTL " + ttl
						+ ";");
		toPrepare.setConsistencyLevel(consistencyLevel);
		return baseCassandraDao.getSession().prepare(toPrepare);
	}
    
	public void batchInsert(SystemLog log) {
		try {
			long startTime = System.currentTimeMillis();
			List<BoundStatement> bstatementList = new ArrayList<BoundStatement>();
			Map<String, Object> values = new HashMap<String, Object>();
			long operateTime = new Date().getTime();
			if (log.getKeyword1()!= null && !log.getKeyword1().isEmpty()) {
				if (preparedwaybill == null)
					preparedwaybill = preparedwaybill();
				BoundStatement bStatement = preparedwaybill.bind(log.getKeyword1(), UUIDs.timeBased().toString(),
						JsonHelper.toJson(log), operateTime);
				bstatementList.add(bStatement);
			}

			baseCassandraDao.batchInsert(bstatementList, values);
			SystemlogCassandra.log.info("OperationlogCassandra batchInsert execute success cost:{}ms",(System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			SystemlogCassandra.log.error("添加操作日志异常 异常原因：", e);
		}
	}
    
	private PreparedStatement preparedSelectBywaybill() {
		RegularStatement toPrepare = new SimpleStatement(
				"select * from systemlogwaybill  where code = ? order by time  ");
		toPrepare.setConsistencyLevel(consistencyLevel);
		return baseCassandraDao.getSession().prepare(toPrepare);
	}

	public List<SystemLog> getPage(String code, Pager<SystemLog> pager) {
		List<SystemLog> list = new ArrayList<SystemLog>();
		long startTime = System.currentTimeMillis();
		try {
			BoundStatement bs = null;
			if (preparedswaybill == null){
				preparedswaybill = preparedSelectBywaybill();
			}
			bs = preparedswaybill.bind(code);
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
			log.info("OperationlogCassandra getPage execute success cost:{}ms" , (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("查询操作日志异常 异常原因：", e);
		}

		return list;
	}
	
	public int totalSize(String code) {
		long startTime = System.currentTimeMillis();
		int size =0;
		try {
			BoundStatement bs = null;
			if (preparedswaybill == null){
                preparedswaybill = preparedSelectBywaybill();
            }
            bs = preparedswaybill.bind(code);
			ResultSet rs = baseCassandraDao.preparedSelectBycode(bs);
			size = rs.getAvailableWithoutFetching();
			log.info("OperationlogCassandra totalSize execute success cost:{}ms" , (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("查询操作日志异常 异常原因：", e);
		}

		return size;
	}
	
	public static ArrayList<SystemLog> rsToList(ResultSet rs, Function<Row, SystemLog> rowToObject) {
		ArrayList<SystemLog> al = new ArrayList<SystemLog>();
		int available = rs.getAvailableWithoutFetching();
		SystemLog log = null;
		for (int i = 0; i < available; i++) {
			Row row = rs.one();
			log = rowToObject.apply(row);
			al.add(log);
		}
		return al;
	}
	
    private static final String BODY="body";
	
	public static final class RowToOrder implements Function<Row, SystemLog> {
		@Override
		public SystemLog apply(Row row) {
			SystemLog log = new SystemLog();
			log = JsonHelper.fromJson(row.getString(BODY), SystemLog.class);
			return log;
		}
	}
}
