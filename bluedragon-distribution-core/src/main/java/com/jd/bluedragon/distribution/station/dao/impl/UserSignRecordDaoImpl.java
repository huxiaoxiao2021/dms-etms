package com.jd.bluedragon.distribution.station.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportSumVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportVo;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;

/**
 * 人员签到表--Dao接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Repository("userSignRecordDao")
public class UserSignRecordDaoImpl extends BaseDao<UserSignRecord> implements UserSignRecordDao {

    private final static String NAMESPACE = UserSignRecordDao.class.getName();
    
	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public int insert(UserSignRecord insertData){
		return this.getSqlSession().insert(NAMESPACE+".insert",insertData);
	}
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public int updateById(UserSignRecord updateData){
		return this.getSqlSession().insert(NAMESPACE+".updateById",updateData);
	}
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	public int deleteById(UserSignRecord deleteData){;
		return this.getSqlSession().insert(NAMESPACE+".deleteById",deleteData);
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public UserSignRecord queryById(Long id){
		return this.getSqlSession().selectOne(NAMESPACE+".queryById",id);
	}
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public List<UserSignRecord> queryList(UserSignRecordQuery query){
	    return this.getSqlSession().selectList(NAMESPACE+".queryList",query);
	}
	/**
	 * 按条件分页查询-导出
	 * @param query
	 * @return
	 */
	public List<UserSignRecord> queryListForExport(UserSignRecordQuery query){
	    return this.getSqlSession().selectList(NAMESPACE+".queryListForExport",query);
	}
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	public long queryCount(UserSignRecordQuery query){
	    return this.getSqlSession().selectOne(NAMESPACE+".queryCount",query);
	}
	@Override
	public List<UserSignRecordReportVo> queryReportList(UserSignRecordQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryReportList",query);
	}
	@Override
	public long queryReportCount(UserSignRecordQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryReportCount",query);
	}
	@Override
	public UserSignRecord queryByBusinessKey(UserSignRecord signInRequest) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryByBusinessKey",signInRequest);
	}
	@Override
	public UserSignRecord queryLastSignRecord(UserSignRecordQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryLastSignRecord",query);
	}
	@Override
	public UserSignRecordReportSumVo queryReportSum(UserSignRecordQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryReportSum",query);
	}

    @Override
    public List<Long> querySignInMoreThanSpecifiedTime(Date signInTime, Integer limit) {
        Map<String, Object> param = new HashMap<>();
        param.put("signInTime", signInTime);
        param.put("limit", limit);
        return this.getSqlSession().selectList(NAMESPACE + ".querySignInMoreThanSpecifiedTime", param);
    }

    @Override
    public int signOutById(UserSignRecord signOutRequest, List<Long> list) {
        Map<String, Object> param = new HashMap<>();
        param.put("param", signOutRequest);
        param.put("list", list);
        return this.getSqlSession().update(NAMESPACE + ".signOutById", param);
    }
}
