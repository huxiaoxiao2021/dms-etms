package com.jd.bluedragon.core.cassandra;

import com.datastax.driver.core.*;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

/**
 * 不要使用此接口保存日志了。请使用统一的日志日志接口com.jd.bluedragon.distribution.log.impl.LogEngineImpl。
 * com.jd.bluedragon.distribution.log.impl.LogEngineImpl 此接口保存的日志会存储到business.log.jd.com 中;
 */
@Deprecated
public class BaseCassandraDao{

    @Value("${cassandra.consistencyLevel.default}")
    protected ConsistencyLevel  consistencyLevel;
    
    @Value("${cassandra.ttl}")
    protected long  ttl;

    private  Session session ;

	public void close(){
        if(this.session.isClosed()){
            return;
        }
        this.session.close();
    }
    
	@JProfiler(jKey = "baseCassandra.batchInsert", mState = { JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError })
	public void batchInsert(List<BoundStatement> bstatementList, Map<String, Object> values) throws Exception {
		BatchStatement batch = new BatchStatement();
		for (BoundStatement statement : bstatementList) {
			batch.add(statement);
		}
		batch.setConsistencyLevel(consistencyLevel);
		session.executeAsync(batch);
	}
	
	@JProfiler(jKey = "baseCassandra.preparedSelectBycode", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
    public ResultSet preparedSelectBycode(BoundStatement bs) throws Exception{
		ResultSet set = session.execute(bs);
        return set;
	}
    
    @JProfiler(jKey = "baseCassandra.insert", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
    public void insert(String tableName,Map<String,Object> values) throws Exception{
        if(StringUtils.isBlank(tableName)){
            throw new Exception("cassandra insert tableName must be not empty");
        }
        if(null==values||values.isEmpty()){
            throw new Exception("cassandra insert values must be not empty");
        }
        String cqlPre="insert into "+tableName;
        String cqlLast=" ";

        Set<String> keys = values.keySet();

        int count=0;
        int size=keys.size();
         Object[] args=new Object[size];
        for (String key:keys){
             if(count==0){
                 cqlPre+=" (" ;
                 cqlLast+=" values(";
             }
            cqlPre+=key;
            cqlLast+="?";
            if(size==(count+1)){
                cqlPre+=")" ;
                cqlLast+=" )";
            }else{
                cqlPre+="," ;
                cqlLast+=" ,";
            }
            args[count]=values.get(key);
            count++;
        }
        String ttlStr=" USING TTL "+ttl;
        String cql=cqlPre+cqlLast+ttlStr;
        RegularStatement toPrepare = new SimpleStatement(cql);
        toPrepare.setConsistencyLevel(consistencyLevel);

        BoundStatement bounded = session.prepare(toPrepare).bind(args);

        ResultSet result = session.execute(bounded);

    }
    @JProfiler(jKey = "baseCassandra.select", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
    public List<Map<String,Object>> select(String tableName,Map<String,Object> params) throws Exception{
        if(StringUtils.isBlank(tableName)){
            throw new Exception("cassandra select tableName must be not empty");
        }
        String cql="select * from "+tableName;
        Object[] args=null;
        if(null!=params&&!params.isEmpty()){
            Set<String> keys = params.keySet();
            int count=0;
            int size=keys.size();
            args=new Object[size];
            for (String key:keys){
                if(count==0){
                    cql+=" where " ;
                }else{
                    cql+=" and " ;
                }
                cql+=key+"=? ";
                args[count]=params.get(key);
                count++;
            }
        }
        RegularStatement toPrepare = new SimpleStatement(cql);
         List<Map<String,Object>> returnResult=null;
         ResultSet result=null;
         if(args!=null){
             BoundStatement bounded = session.prepare(toPrepare).bind(args);
             result = session.execute(bounded);
         }else{
             result = session.execute(toPrepare);
         }
         if(null!=result){
             returnResult=transferRowToMap(result.all());
         }

        return returnResult;
    }
    public List<Object> transferRowToBean(List<Row> all,Class c) throws Exception {
        if(null==all||all.size()<1){
            return null;
        }
        List<Object> objList=new ArrayList<Object>();
        for(Row row:all){
            if(null!=row){
                Map<String,Object> map=new HashMap<String, Object>();
                List<ColumnDefinitions.Definition> columes = row.getColumnDefinitions().asList();
                if(null!=columes&&columes.size()>0){
                    for(ColumnDefinitions.Definition colume:columes){
                        map.put(colume.getName(),row.getObject(colume.getName()));
                    }
                }
                objList.add(ObjectMapHelper.transMap2Bean(map, c));
            }
        }
        return objList;
    }
    
    public List<? extends Object> transferRowToClassBean(List<Row> all,Class c) throws Exception {
        if(null==all||all.size()<1){
            return null;
        }
        List<Object> objList=new ArrayList<Object>();
        for(Row row:all){
            if(null!=row){
                Map<String,Object> map=new HashMap<String, Object>();
                List<ColumnDefinitions.Definition> columes = row.getColumnDefinitions().asList();
                if(null!=columes&&columes.size()>0){
                    for(ColumnDefinitions.Definition colume:columes){
                        map.put(colume.getName(),row.getObject(colume.getName()));
                    }
                }
                objList.add(ObjectMapHelper.transMap2Bean(map, c));
            }
        }
        return objList;
    }
    
    public List<Map<String, Object>> transferRowToMap(List<Row> all) {
        if(null==all||all.size()<1){
            return null;
        }
        List<Map<String,Object>> mapList=new ArrayList<Map<String, Object>>();
        for(Row row:all){
            if(null!=row){
                Map<String,Object> map=new HashMap<String, Object>();
                List<ColumnDefinitions.Definition> columes = row.getColumnDefinitions().asList();
                if(null!=columes&&columes.size()>0){
                      for(ColumnDefinitions.Definition colume:columes){
                          map.put(colume.getName(),row.getObject(colume.getName()));
                      }
                }
                mapList.add(map);
            }
        }
        return mapList;
    }

    @JProfiler(jKey = "baseCassandra.insertByCql", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
    public void insertByCql(String cql,Object[] args) throws Exception{
        if(StringUtils.isBlank(cql)){
            throw new Exception("cassandra insertByCql cql must be not empty");
        }
        if(!cql.startsWith("insert")){
            throw new Exception("cassandra insertByCql cql must be not select cql");
        }
         if(!(cql.contains("USING TTL")||cql.contains("using ttl"))){
             String ttlStr=" USING TTL "+ttl;
             cql=cql+ttlStr;
         }
        RegularStatement toPrepare = new SimpleStatement(cql);
        toPrepare.setConsistencyLevel(consistencyLevel);
        if(args!=null){
           BoundStatement bounded = session.prepare(toPrepare).bind(args);
           session.execute(bounded);
        }else{
           session.execute(toPrepare);
        }

    }
    
    @JProfiler(jKey = "baseCassandra.selectByCql", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
    public List<Map<String,Object>> selectByCql(String cql,Object[] args) throws Exception{
        if(StringUtils.isBlank(cql)){
            throw new Exception("cassandra selectByCql cql must be not empty");
        }
        if(!cql.startsWith("select")){
            throw new Exception("cassandra selectByCql cql must be not select cql");
        }
        RegularStatement toPrepare = new SimpleStatement(cql);
        List<Map<String,Object>> returnResult=null;
        ResultSet result=null;
        if(args!=null){
            BoundStatement bounded = session.prepare(toPrepare).bind(args);
            result = session.execute(bounded);
        }else{
            result = session.execute(toPrepare);
        }
        if(null!=result){
            returnResult=transferRowToMap(result.all());
        }

        return returnResult;
    }
    
    @JProfiler(jKey = "baseCassandra.selectByCqlReturnBean", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
    public List<? extends Object> selectByCqlReturnBean(String cql,Object[] args,Class c) throws Exception{
        if(StringUtils.isBlank(cql)){
            throw new Exception("cassandra selectByCqlReturnBean cql must be not empty");
        }
        if(!cql.startsWith("select")){
            throw new Exception("cassandra selectByCqlReturnBean cql must be not select cql");
        }
        RegularStatement toPrepare = new SimpleStatement(cql);
        List<Object> returnResult=null;
        ResultSet result=null;
        if(args!=null){
            BoundStatement bounded = session.prepare(toPrepare).bind(args);
            result = session.execute(bounded);
        }else{
            result = session.execute(toPrepare);
        }
        if(null!=result){
            returnResult=transferRowToBean(result.all(),c);
        }

        return returnResult;
    }
    
    @JProfiler(jKey = "baseCassandra.executeByCql", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
    public ResultSet executeByCql(String cql,Object[] args) throws Exception{
        if(StringUtils.isBlank(cql)){
            throw new Exception("cassandra executeByCql cql must be not empty");
        }

        RegularStatement toPrepare = new SimpleStatement(cql);
        ResultSet result=null;
        if(args!=null){
            BoundStatement bounded = session.prepare(toPrepare).bind(args);
            result = session.execute(bounded);
        }else{
            result = session.execute(toPrepare);
        }
        return result;
    }

    public void setSession(Session session) {
        this.session = session;
    }
    
    public Session getSession() {
		return session;
	}
}
