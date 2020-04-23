package com.jd.bluedragon.distribution.operationLog.dao;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Function;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.cassandra.BaseCassandraDao;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.*;

public class OperationlogCassandra {

	private static final Logger log = LoggerFactory.getLogger(OperationlogCassandra.class);

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
				BoundStatement bStatement = getPreparedwaybill().bind(log.getWaybillCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJsonUseGson(log), operateTime);
				bstatementList.add(bStatement);
			}
			if (log.getBoxCode() != null && !log.getBoxCode().isEmpty()) {
				BoundStatement bStatement = getPreparedbox().bind(log.getBoxCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJsonUseGson(log), operateTime);
				bstatementList.add(bStatement);
			}
			if (log.getPackageCode() != null && !log.getPackageCode().isEmpty()) {
				BoundStatement bStatement = getPreparedpackage().bind(log.getPackageCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJsonUseGson(log), operateTime);
				bstatementList.add(bStatement);
			}
			if (log.getPickupCode() != null && !log.getPickupCode().isEmpty()) {
				BoundStatement bStatement = getPreparedpickCode().bind(log.getPickupCode(), UUIDs.timeBased().toString(),
						JsonHelper.toJsonUseGson(log), operateTime);
				bstatementList.add(bStatement);
			}

			baseCassandraDao.batchInsert(bstatementList, values);
			OperationlogCassandra.log.info("OperationlogCassandra batchInsert execute success cost:{}ms",(System.currentTimeMillis() - startTime) );
		} catch (Exception e) {
			OperationlogCassandra.log.error("添加操作日志异常 异常原因：{}",JsonHelper.toJson(log), e);
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
				bs = getPreparedswaybill().bind(code);
			}
			if (type.equals("pick")) {
                bs = getPreparedspickCode().bind(code);
			}
            if (type.equals("package")){
                bs = getPreparedspackage().bind(code);
			}
			if (type.equals("box")){
                bs = getPreparedsbox().bind(code);
			}
			if(bs == null){
				log.warn("Cassandra操作日志查询，未知的参数类型：{}" , type);
				return list;
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
			log.info("OperationlogCassandra getPage execute success cost:{}ms" , (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("查询操作日志异常 异常原因：code={}",code, e);
		}

		return list;
	}
	
	public int totalSize(String code, String type) {
		long startTime = System.currentTimeMillis();
		int size =0;
		try {
			BoundStatement bs = null;
			if (type.equals("waybill")) {
				bs = getPreparedswaybill().bind(code);
			}
			if (type.equals("pick")) {
				bs = getPreparedspickCode().bind(code);
			}
			if (type.equals("package")){
				bs = getPreparedspackage().bind(code);
			}
			if (type.equals("box")){
				bs = getPreparedsbox().bind(code);
			}
			ResultSet rs = baseCassandraDao.preparedSelectBycode(bs);
			size = rs.getAvailableWithoutFetching();
			log.info("OperationlogCassandra totalSize execute success cost:{}ms" , (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("查询操作日志异常 异常原因：code={}",code, e);
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

    private PreparedStatement getPreparedbox() {
        if (preparedbox == null)
            preparedbox = preparedbox();
        return preparedbox;
    }

    private PreparedStatement getPreparedpackage() {
        if (preparedpackage == null)
            preparedpackage = preparedpackage();
        return preparedpackage;
    }

    private PreparedStatement getPreparedpickCode() {
        if (preparedpickCode == null)
            preparedpickCode = preparedpickCode();
        return preparedpickCode;
    }

    private PreparedStatement getPreparedsbox() {
        if (preparedsbox == null)
            preparedsbox = preparedSelectByboxcode();
        return preparedsbox;
    }

    private PreparedStatement getPreparedspackage() {
        if (preparedspackage == null)
            preparedspackage = preparedSelectBypackagecode();
        return preparedspackage;
    }

    private PreparedStatement getPreparedspickCode() {
        if (preparedspickCode == null)
            preparedspickCode = preparedSelectBypickcode();
        return preparedspickCode;
    }

    private PreparedStatement getPreparedswaybill() {
        if (preparedswaybill == null)
            preparedswaybill = preparedSelectBywaybill();
        return preparedswaybill;
    }

    private PreparedStatement getPreparedwaybill() {
        if (preparedwaybill == null)
            preparedwaybill = preparedwaybill();
        return preparedwaybill;
    }

}
